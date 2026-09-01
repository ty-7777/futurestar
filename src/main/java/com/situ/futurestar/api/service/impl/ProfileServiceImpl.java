package com.situ.futurestar.api.service.impl;

import com.situ.futurestar.api.service.ProfileService;
import com.situ.futurestar.core.dto.ChangePasswordDTO;
import com.situ.futurestar.core.dto.UpdateProfileDTO;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.ProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.bucket}")
    private String bucket;
    @Value("${oss.access-key-id}")
    private String accessKeyId;
    @Value("${oss.access-key-secret}")
    private String accessKeySecret;
    @Override
    public ProfileVO getProfile() {
        ProfileVO vo =new ProfileVO();
        User currentUser = SecurityUtil.getCurrentUser();
        BeanUtils.copyProperties(currentUser,vo);
        return vo;
    }

    @Override
    public void updateProfile(UpdateProfileDTO updateProfileDTO) {
        if(updateProfileDTO==null){
            throw new BizException("上传的个人信息列表不能为空");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        int updated = userMapper.updateById(userId,updateProfileDTO);
        if(updated!=1){
            throw new BizException("该用户不存在");
        }

    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        String oldPassword = changePasswordDTO.getOldPassword();
        String newPassword = changePasswordDTO.getNewPassword();;
        String password = SecurityUtil.getCurrentUser().getPassword();
        if(!passwordEncoder.matches(oldPassword,password)){
            throw new BizException("原密码错误");
        }
        if(oldPassword.equals(newPassword)){
            throw new BizException("原密码不能和旧密码一样");
        }
        //走到这里说明具备修改条件
        userMapper.updatePassword(SecurityUtil.getCurrentUserId(),passwordEncoder.encode(newPassword));

    }

    @Override
    public Map<String, String> getAvatarUploadPolicy() {
        String dir = "avatar/" + SecurityUtil.getCurrentUserId() + "/";
        // endpoint 形如 oss-cn-qingdao.aliyuncs.com，V4 签名需要其中的 region 段
        String region = endpoint.replaceFirst("^oss-", "").split("\\.")[0];
        long expireEndTime = System.currentTimeMillis() + 300 * 1000;   // 策略有效期 5 分钟
        Instant now = Instant.now();

        // 阿里云已对新 Bucket 禁用 V1 签名，必须用 V4（OSS4-HMAC-SHA256）
        String dateTime = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'")
                .withZone(ZoneOffset.UTC).format(now);
        String date = dateTime.substring(0, 8);
        String scope = date + "/" + region + "/oss/aliyun_v4_request";   // credential 的日期后三段，也是派生链的层级
        String credential = accessKeyId + "/" + scope;

        // OSS 要求 expiration 为 ISO8601 UTC 时间；条件限制大小 2MB + 只能传到自己的目录（服务端强制，防绕过）
        // 注意：V4 要求 policy 的 conditions 里必须包含 signature-version/credential/date 三个字段，
        // OSS 校验签名时会从 policy 里取它们重建待签名信息，缺了就会 SignatureDoesNotMatch
        String expiration = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC)
                .format(Instant.ofEpochMilli(expireEndTime));
        String policyJson = "{\"expiration\":\"" + expiration + "\",\"conditions\":["
                + "{\"x-oss-signature-version\":\"OSS4-HMAC-SHA256\"},"
                + "{\"x-oss-credential\":\"" + credential + "\"},"
                + "{\"x-oss-date\":\"" + dateTime + "\"},"
                + "[\"content-length-range\",0," + 2 * 1024 * 1024 + "],"
                + "[\"starts-with\",\"$key\",\"" + dir + "\"]]}";
        // POST V4 的 StringToSign 就是 Base64(policy) 本身（与 API 请求的 V4 不同，没有前缀行）
        String policy = Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
        // 签名密钥逐层派生：secret -> date -> region -> "oss" -> "aliyun_v4_request"
        byte[] signingKey = hmacSha256(("aliyun_v4" + accessKeySecret).getBytes(StandardCharsets.UTF_8), date);
        signingKey = hmacSha256(signingKey, region);
        signingKey = hmacSha256(signingKey, "oss");
        signingKey = hmacSha256(signingKey, "aliyun_v4_request");
        String signature = bytesToHex(hmacSha256(signingKey, policy));

        Map<String, String> result = new HashMap<>();
        result.put("accessId", accessKeyId);
        result.put("policy", policy);
        result.put("signature", signature);
        result.put("credential", credential);
        result.put("dateTime", dateTime);
        result.put("host", "https://" + bucket + "." + endpoint);
        result.put("dir", dir);
        return result;
    }

    private byte[] hmacSha256(byte[] key, String content) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new BizException("生成OSS上传签名失败");
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

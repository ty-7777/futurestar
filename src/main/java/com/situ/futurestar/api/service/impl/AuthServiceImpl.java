package com.situ.futurestar.api.service.impl;

import com.situ.futurestar.api.service.AuthService;
import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.*;
import com.situ.futurestar.core.entity.RefreshToken;
import com.situ.futurestar.core.entity.SmsCode;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.RefreshTokenMapper;
import com.situ.futurestar.core.mapper.SmsCodeMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.JwtUtil;
import com.situ.futurestar.core.vo.LoginVO;
import com.situ.futurestar.core.vo.TokenVO;
import com.situ.futurestar.core.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final SmsCodeMapper smsCodeMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenMapper refreshTokenMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final  String  BLACK_LIST_TOKEN ="jwt:blacklist:";
    @Override
    public Result<Void> sendCode(SendCodeDTO sendCodeDTO) {
        String phone = sendCodeDTO.getPhone();
        if(phone == null|| phone.isEmpty()){
            throw  new BizException("手机号不能为空");
        }
        //TODO: 调用api给用户手机发送验证码
        SmsCode smsCode=new SmsCode();
        smsCode.setUsed(false);
        smsCode.setPhone(phone);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(2));//设置过期时间两分钟

        //把验明码存入数据库中
        smsCodeMapper.insert(smsCode);
        return Result.success();
    }

    @Override
    public Result<Void> register(RegisterDTO registerDTO) {
        //校验验证码正确性
        String code = registerDTO.getCode();
        String phone = registerDTO.getPhone();
        SmsCode latestValid = smsCodeMapper.selectLatestValid(phone);
        if (latestValid == null //不存在验证码
                || !latestValid.getExpireTime().isAfter(LocalDateTime.now()) //过期
                || latestValid.getUsed()//被用过
                ||!latestValid.getCode().equals(code)//验证码不正确
        ) {
            throw  new BizException("验证码不合法,请重试");
        }
        //校验手机号是否已经存在
        User selected = userMapper.selectByPhone(phone);
        if(selected!=null){
            throw  new BizException("手机号已存在");
        }
        //BCrypt加密密码
        String encode = passwordEncoder.encode(registerDTO.getPassword());
        //创建用户
        User user =new User();
        user.setPhone(phone);
        user.setPassword(encode);
        user.setPoints(100);//赠送初始积分100
        user.setRole("PLAYER");//设置角色，默认是球员
        user.setMemberLevel("NORMAL");   // 建议一并显式设
        user.setStatus("ENABLED");
        userMapper.insert(user);
        smsCodeMapper.markUsed(latestValid.getId());//标记验证码被使用
        return Result.success();
    }

    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        //1.组装未认证的Token（传入手机号，明文密码）
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getPhone(),loginDTO.getPassword());
        //2.交给AuthenticationManager校验，内部调用UserDetailsService + PasswordEncoder
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 走到这里代表认证成功；authentication里面包含登录用户信息
        // 获取登录的手机号
        String phone = authenticate.getName();
        //查询用户id
        User user = userMapper.selectByPhone(phone);
        if(!"ENABLED".equals(user.getStatus())){
            throw new BizException(ErrorCode.FORBIDDEN,"账号已被禁用");
        }
        Long userId = user.getId();
        //生成accessToken
        String token = jwtUtil.generateToken(userId, phone);
        //生成refreshToken
        String refreshToken = jwtUtil.generateRefreshToken(userId, phone);
        //把refreshToken存入数据库
        RefreshToken rfToken =new RefreshToken();
        rfToken.setToken(refreshToken);
        rfToken.setUserId(userId);
        rfToken.setExpireTime(LocalDateTime.now().plusDays(7));
        refreshTokenMapper.save(rfToken);
        //构建返回值VO
        LoginVO loginVO =new LoginVO();
        UserVO userVO=new UserVO();
        BeanUtils.copyProperties(user,userVO);
        loginVO.setUser(userVO);
        loginVO.setAccessToken(token);
        loginVO.setRefreshToken(refreshToken);

        return Result.success(loginVO);
    }

    @Override
    public Result<TokenVO> refresh(String refreshToken) {
        //1.校验refreshToken的有效性,查询数据库确认
        RefreshToken selected = refreshTokenMapper.selectByToken(refreshToken);
        if(selected==null){
            throw new BizException("refreshToken不存在");
        }
        Long userId = selected.getUserId();
        User user = userMapper.selectById(userId);
        String phone = user.getPhone();
        //生成新的AccessToken
        String accessToken = jwtUtil.generateToken(userId, phone);
        TokenVO tokenVO =new TokenVO();
        tokenVO.setAccessToken(accessToken);
        tokenVO.setRefreshToken(refreshToken);
        return Result.success(tokenVO);
    }

    @Override
    public void logout(String token) {
        //1.将Access Token加入Redis黑名单
        //先拿到token的唯一id
        String jti = jwtUtil.parseJti(token);
        Long userId = jwtUtil.parseUserId(token);
        //把当前的accessToken放入redis的黑名单中
        stringRedisTemplate.opsForValue().set(
                BLACK_LIST_TOKEN+jti,"1",
                Duration.ofMillis(jwtUtil.getRemainingMills(token))
        );
        //将RefreshToken从数据库中删除
        refreshTokenMapper.deleteByUserId(userId);

    }

    @Override
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String code = resetPasswordDTO.getCode();
        String phone = resetPasswordDTO.getPhone();
        User user = userMapper.selectByPhone(phone);
        if(user==null){
            throw  new BizException("用户不存在");
        }
        SmsCode latestValid = smsCodeMapper.selectLatestValid(phone);
        if (latestValid == null //不存在验证码
                || !latestValid.getExpireTime().isAfter(LocalDateTime.now()) //过期
                || latestValid.getUsed()//被用过
                ||!latestValid.getCode().equals(code)//验证码不正确
        ) {
            throw  new BizException("验证码不合法,请重试");
        }
        //标记验证码已使用
        smsCodeMapper.markUsed(latestValid.getId());
        //加密新密码
        String encode = passwordEncoder.encode(resetPasswordDTO.getPassword());
        //保存新密码
        userMapper.updatePassword(user.getId(), encode);
        //删除用户的所有token强制用户重新登录
        Long userId = user.getId();
        refreshTokenMapper.deleteByUserId(userId);

    }


}

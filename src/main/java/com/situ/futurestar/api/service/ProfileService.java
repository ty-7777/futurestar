package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.ChangePasswordDTO;
import com.situ.futurestar.core.dto.UpdateProfileDTO;
import com.situ.futurestar.core.vo.ProfileVO;

import java.util.Map;

public interface ProfileService {
    ProfileVO getProfile();

    void updateProfile(UpdateProfileDTO updateProfileDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    /** 生成头像上传的OSS签名直传策略 */
    Map<String, String> getAvatarUploadPolicy();
}

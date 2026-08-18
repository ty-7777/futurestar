package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.ChangePasswordDTO;
import com.situ.futurestar.core.dto.UpdateProfileDTO;
import com.situ.futurestar.core.vo.ProfileVO;

public interface ProfileService {
    ProfileVO getProfile();

    void updateProfile(UpdateProfileDTO updateProfileDTO);

    void changePassword(ChangePasswordDTO changePasswordDTO);
}

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
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
}

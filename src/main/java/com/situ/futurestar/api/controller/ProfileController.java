package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.ProfileService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.ChangePasswordDTO;
import com.situ.futurestar.core.dto.UpdateProfileDTO;
import com.situ.futurestar.core.vo.ProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/profile")
public class ProfileController {
    private final ProfileService profileService;
    //获取个人信息
    @GetMapping()
    public Result<ProfileVO> getProfile(){
        return Result.success(profileService.getProfile());
    }
    //更新个人信息
    @PutMapping()
    public Result<Void> updateProfile(@RequestBody UpdateProfileDTO updateProfileDTO){
        profileService.updateProfile(updateProfileDTO);
        return Result.success();
    }
    //修改密码
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid  @RequestBody ChangePasswordDTO changePasswordDTO){
        profileService.changePassword(changePasswordDTO);
        return Result.success();
    }
    //获取头像上传的OSS签名直传策略
    @GetMapping("/oss-policy")
    public Result<Map<String, String>> ossPolicy(){
        return Result.success(profileService.getAvatarUploadPolicy());
    }
}

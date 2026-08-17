package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.AuthService;
import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.*;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.vo.LoginVO;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.TokenVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping ("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final  AuthService authService;

    //发送验证码
    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDTO sendCodeDTO ){
        return authService.sendCode(sendCodeDTO);
    }
    //注册
    @PostMapping("/register")
    public Result<Void> register(@Valid@RequestBody RegisterDTO registerDTO){
        return authService.register(registerDTO);
    }
    //登录
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid@RequestBody LoginDTO loginDTO){
        return authService.login(loginDTO);
    }
    //刷新Token
    @PostMapping("/refresh")
    public Result<TokenVO> refresh(@RequestBody Map<String,String> body){
        String refreshToken = body.get("refreshToken");
        if(refreshToken==null||refreshToken.isBlank()){
            throw new BizException("token为空");
        }
        return authService.refresh(refreshToken);
    }
    //登出
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authorization){
        String token = authorization.substring(7);
        authService.logout(token);
        return Result.success();
    }
    //密码找回
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO){
        authService.resetPassword(resetPasswordDTO);
        return Result.success();
    }


}

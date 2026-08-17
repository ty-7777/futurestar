package com.situ.futurestar.core.vo;

import lombok.Data;

/**
 * 登录/注册成功响应 VO
 */
@Data
public class LoginVO {

    /** 访问令牌（Access Token） */
    private String accessToken;

    /** 刷新令牌（Refresh Token） */
    private String refreshToken;

    /** 用户信息 */
    private UserVO user;
}

package com.situ.futurestar.core.vo;

import lombok.Data;

/**
 * 令牌刷新响应 VO
 */
@Data
public class TokenVO {

    /** 访问令牌（Access Token） */
    private String accessToken;

    /** 刷新令牌（Refresh Token） */
    private String refreshToken;
}

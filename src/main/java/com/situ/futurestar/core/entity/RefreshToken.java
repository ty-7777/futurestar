package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 刷新令牌实体类
 * 对应数据库表：refresh_token（刷新令牌表）
 */
@Data
public class RefreshToken {

    /** 记录ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** Refresh Token值 */
    private String token;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}

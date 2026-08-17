package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 短信验证码实体类
 * 对应数据库表：sms_code（短信验证码表）
 */
@Data
public class SmsCode {

    /** 记录ID（主键，自增） */
    private Long id;

    /** 手机号 */
    private String phone;

    /** 验证码 */
    private String code;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 是否已使用：0 未使用 / 1 已使用 */
    private Boolean used;

    /** 创建时间 */
    private LocalDateTime createTime;
}

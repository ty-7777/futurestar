package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛事报名实体类
 * 对应数据库表：event_registration（赛事报名表）
 */
@Data
public class EventRegistration {

    /** 报名ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 活动ID */
    private Long eventId;

    /** 签到状态：NOT_CHECKED_IN 未签到 / CHECKED_IN 已签到 */
    private String checkInStatus;

    /** 报名时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

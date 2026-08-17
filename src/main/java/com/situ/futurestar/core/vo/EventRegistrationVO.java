package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛事报名 VO（含球员信息，用于管理端报名列表）
 */
@Data
public class EventRegistrationVO {

    /** 报名ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 球员姓名 */
    private String playerName;

    /** 球员手机号 */
    private String phone;

    /** 场上位置 */
    private String position;

    /** 会员等级 */
    private String memberLevel;

    /** 签到状态：NOT_CHECKED_IN / CHECKED_IN */
    private String checkInStatus;

    /** 报名时间 */
    private LocalDateTime createTime;
}

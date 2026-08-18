package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理端球员列表项 VO（不含密码）
 */
@Data
public class AdminPlayerVO {

    /** 用户ID */
    private Long id;

    /** 手机号 */
    private String phone;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private String gender;

    /** 场上位置 */
    private String position;

    /** 惯用脚 */
    private String preferredFoot;

    /** 球龄（年） */
    private Integer experienceYears;

    /** 会员等级 */
    private String memberLevel;

    /** 积分 */
    private Integer points;

    /** 状态：ENABLED / DISABLED */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;
}

package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 会员个人中心信息 VO
 */
@Data
public class ProfileVO {

    /** 用户ID */
    private Long id;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private String gender;

    /** 出生日期 */
    private LocalDate birthDate;

    /** 身高（cm） */
    private Double height;

    /** 体重（kg） */
    private Double weight;

    /** 场上位置 */
    private String position;

    /** 惯用脚 */
    private String preferredFoot;

    /** 球龄（年） */
    private Integer experienceYears;

    /** 头像 URL */
    private String avatar;

    /** 紧急联系人电话 */
    private String emergencyContact;

    /** 会员等级 */
    private String memberLevel;

    /** 积分 */
    private Integer points;
}

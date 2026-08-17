package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户信息 VO（不含密码等敏感字段）
 */
@Data
public class UserVO {

    /** 用户ID */
    private Long id;

    /** 手机号 */
    private String phone;

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

    /** 状态：ENABLED / DISABLED */
    private String status;

    /** 角色：PLAYER / ADMIN */
    private String role;
}

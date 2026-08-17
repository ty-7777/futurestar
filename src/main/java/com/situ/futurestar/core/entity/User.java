package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：user（用户表：球员 + 管理员）
 */
@Data
public class User {

    /** 用户ID（主键，自增） */
    private Long id;

    /** 手机号（唯一，用于登录） */
    private String phone;

    /** 密码（BCrypt 加密存储） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private String gender;

    /** 出生日期 */
    private LocalDate birthDate;

    /** 身高（cm），用于计算 BMI */
    private Double height;

    /** 体重（kg），用于计算 BMI */
    private Double weight;

    /** 场上位置：FORWARD 前锋 / MIDFIELDER 中场 / DEFENDER 后卫 / GOALKEEPER 门将 */
    private String position;

    /** 惯用脚：LEFT 左脚 / RIGHT 右脚 / BOTH 双脚 */
    private String preferredFoot;

    /** 球龄（年） */
    private Integer experienceYears;

    /** 头像 URL */
    private String avatar;

    /** 紧急联系人电话 */
    private String emergencyContact;

    /** 会员等级：NORMAL / SILVER / GOLD / PLATINUM / DIAMOND */
    private String memberLevel;

    /** 积分 */
    private Integer points;

    /** 状态：ENABLED 启用 / DISABLED 禁用 */
    private String status;

    /** 角色：PLAYER 球员 / ADMIN 管理员 */
    private String role;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

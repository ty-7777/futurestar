package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 体能记录实体类
 * 对应数据库表：physical_record（体能记录表）
 */
@Data
public class PhysicalRecord {

    /** 记录ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 身高（cm） */
    private Double height;

    /** 体重（kg） */
    private Double weight;

    /** BMI指数 */
    private Double bmi;

    /** 体脂率（%） */
    private Double bodyFatRate;

    /** 静息心率（次/分） */
    private Integer heartRate;

    /** 肺活量（ml） */
    private Integer vitalCapacity;

    /** 30米冲刺（秒） */
    private Double sprint30m;

    /** 立定跳远（cm） */
    private Double standingLongJump;

    /** 原地纵跳（cm） */
    private Double verticalJump;

    /** 12分钟耐力跑（米） */
    private Integer enduranceRun;

    /** 备注 */
    private String memo;

    /** 记录时间 */
    private LocalDateTime recordedAt;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

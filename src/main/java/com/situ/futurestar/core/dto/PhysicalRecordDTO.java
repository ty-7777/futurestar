package com.situ.futurestar.core.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 录入体能记录请求 DTO
 * BMI 由服务端根据身高体重自动计算，无需前端传入
 */
@Data
public class PhysicalRecordDTO {

    /** 身高（cm） */
    private Double height;

    /** 体重（kg） */
    private Double weight;

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

    /** 记录时间（可选，不传默认当前时间，用于补录历史数据） */
    private LocalDateTime recordedAt;

    /** 备注 */
    private String memo;
}

package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程预约 VO（含课程/时段关联信息）
 */
@Data
public class CourseAppointmentVO {

    /** 预约ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 课程套餐ID */
    private Long packageId;

    /** 课程名称 */
    private String packageName;

    /** 授课教练 */
    private String coachName;

    /** 时段ID */
    private Long slotId;

    /** 课程日期 */
    private LocalDate courseDate;

    /** 时间段 */
    private String timeRange;

    /** 状态：PENDING / CONFIRMED / CANCELED / COMPLETED */
    private String status;

    /** 训练/表现报告URL */
    private String reportUrl;

    /** 创建时间 */
    private LocalDateTime createTime;
}

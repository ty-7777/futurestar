package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程预约实体类
 * 对应数据库表：course_appointment（课程预约表）
 */
@Data
public class CourseAppointment {

    /** 预约ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 时间段ID */
    private Long slotId;

    /** 课程套餐ID */
    private Long packageId;

    /** 状态：PENDING 待确认 / CONFIRMED 已确认 / CANCELED 已取消 / COMPLETED 已完成 */
    private String status;

    /** 训练/表现报告URL */
    private String reportUrl;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

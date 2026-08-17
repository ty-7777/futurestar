package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程时段实体类
 * 对应数据库表：course_slot（课程时段表）
 */
@Data
public class CourseSlot {

    /** 时间段ID（主键，自增） */
    private Long id;

    /** 课程套餐ID */
    private Long packageId;

    /** 课程日期 */
    private LocalDate courseDate;

    /** 时间段，如 18:00-19:30 */
    private String timeRange;

    /** 最大预约人数 */
    private Integer maxCount;

    /** 当前已预约人数 */
    private Integer currentCount;

    /** 状态：AVAILABLE 可约 / FULL 已满 / CLOSED 已关闭 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

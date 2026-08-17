package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 课程时段 VO（含剩余名额）
 */
@Data
public class CourseSlotVO {

    /** 时间段ID */
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

    /** 剩余名额 = 最大人数 - 已预约人数 */
    private Integer remaining;

    /** 状态：AVAILABLE / FULL / CLOSED */
    private String status;
}

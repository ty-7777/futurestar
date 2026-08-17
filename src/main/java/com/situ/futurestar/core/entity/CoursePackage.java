package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程套餐实体类
 * 对应数据库表：course_package（课程套餐表）
 */
@Data
public class CoursePackage {

    /** 套餐ID（主键，自增） */
    private Long id;

    /** 课程名称 */
    private String name;

    /** 封面图URL */
    private String coverUrl;

    /** 课程描述 */
    private String description;

    /** 价格（积分抵扣） */
    private Integer price;

    /** 授课教练 */
    private String coachName;

    /** 适合水平 */
    private String suitableLevel;

    /** 包含训练项目JSON */
    private String items;

    /** 状态：ENABLED 启用 / DISABLED 禁用 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

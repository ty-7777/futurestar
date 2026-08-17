package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 课程套餐新增/修改请求 DTO（管理端）
 */
@Data
public class CoursePackageDTO {

    /** 课程名称 */
    @NotBlank(message = "课程名称不能为空")
    private String name;

    /** 封面图URL */
    private String coverUrl;

    /** 课程描述 */
    private String description;

    /** 价格（积分抵扣） */
    @NotNull(message = "价格不能为空")
    private Integer price;

    /** 授课教练 */
    private String coachName;

    /** 适合水平 */
    private String suitableLevel;

    /** 包含训练项目JSON */
    private String items;

    /** 状态：ENABLED / DISABLED */
    private String status;
}

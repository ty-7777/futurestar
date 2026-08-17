package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 批量生成课程时段请求 DTO（管理端）
 */
@Data
public class BatchSlotDTO {

    /** 开始日期 */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /** 结束日期 */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /** 时间段，如 18:00-19:30 */
    @NotBlank(message = "时间段不能为空")
    private String timeRange;

    /** 每时段最大人数 */
    @NotNull(message = "最大人数不能为空")
    @Min(value = 1, message = "最大人数至少为1")
    @Max(value = 100, message = "最大人数不能超过100")
    private Integer maxCount;
}

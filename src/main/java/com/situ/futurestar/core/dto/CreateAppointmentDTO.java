package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交课程预约请求 DTO
 */
@Data
public class CreateAppointmentDTO {

    /** 时间段 ID */
    @NotNull(message = "时段ID不能为空")
    private Long slotId;
}

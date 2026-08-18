package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 启用/禁用球员请求 DTO
 */
@Data
public class PlayerStatusDTO {

    /** ENABLED / DISABLED */
    @NotBlank(message = "状态不能为空")
    private String status;
}

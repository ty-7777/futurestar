package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 调整球员积分请求 DTO
 */
@Data
public class PlayerPointsDTO {

    /** 积分变更量（正加负减） */
    @NotNull(message = "积分变更量不能为空")
    private Integer delta;

    /** 变更原因 */
    private String reason;
}

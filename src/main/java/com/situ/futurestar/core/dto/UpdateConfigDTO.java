package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新系统配置请求 DTO（管理端）
 */
@Data
public class UpdateConfigDTO {

    /** 配置值 */
    @NotNull(message = "配置值不能为空")
    private String configValue;
}

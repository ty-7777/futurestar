package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 调整球员等级请求 DTO
 */
@Data
public class PlayerLevelDTO {

    /** NORMAL / SILVER / GOLD / PLATINUM / DIAMOND */
    @NotBlank(message = "会员等级不能为空")
    private String memberLevel;
}

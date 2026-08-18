package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 问卷发布/下架请求 DTO（管理端）
 */
@Data
public class QuestionnaireStatusDTO {

    /** DRAFT 下架 / PUBLISHED 发布 */
    @NotBlank(message = "状态不能为空")
    private String status;
}

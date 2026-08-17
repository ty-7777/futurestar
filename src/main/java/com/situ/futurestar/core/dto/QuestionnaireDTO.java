package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 问卷新增/修改请求 DTO（管理端）
 */
@Data
public class QuestionnaireDTO {

    /** 问卷标题 */
    @NotBlank(message = "问卷标题不能为空")
    private String title;

    /** 问卷描述 */
    private String description;

    /** 状态：DRAFT / PUBLISHED */
    private String status;
}

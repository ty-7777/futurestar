package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交评测请求 DTO
 */
@Data
public class SubmitAssessmentDTO {

    /** 问卷 ID */
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    /** 答案快照（JSON） */
    private String answers;
}

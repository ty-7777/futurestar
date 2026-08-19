package com.situ.futurestar.core.entity;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

@Data
public class AiAssessmentResult {
    @JsonPropertyDescription("百分制评分，0-100 的整数")
    private Integer score;

    @JsonPropertyDescription("针对学员的个性化训练/饮食建议，中文文本")
    private String suggestion;
}

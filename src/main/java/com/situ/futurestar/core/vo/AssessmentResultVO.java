package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评测结果 VO
 */
@Data
public class AssessmentResultVO {

    /** 评测结果ID */
    private Long id;

    /** AI评分（百分制） */
    private Integer aiScore;

    /** AI建议 */
    private String aiSuggestion;

    /** 答案快照（JSON） */
    private String answers;

    /** 创建时间 */
    private LocalDateTime createTime;
}

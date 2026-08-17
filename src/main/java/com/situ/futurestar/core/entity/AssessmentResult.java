package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评测结果实体类
 * 对应数据库表：assessment_result（评测结果表）
 */
@Data
public class AssessmentResult {

    /** 评测结果ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 问卷ID */
    private Long questionnaireId;

    /** 答案快照JSON */
    private String answers;

    /** AI评分（百分制） */
    private Integer aiScore;

    /** AI建议 */
    private String aiSuggestion;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

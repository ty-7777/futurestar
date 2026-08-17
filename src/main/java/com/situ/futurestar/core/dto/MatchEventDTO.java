package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛事活动新增/修改请求 DTO（管理端）
 */
@Data
public class MatchEventDTO {

    /** 活动标题 */
    @NotBlank(message = "活动标题不能为空")
    private String title;

    /** 类型：MATCH / CAMP / SELECTION */
    private String type;

    /** 封面图URL */
    private String coverUrl;

    /** 活动内容 */
    private String content;

    /** 报名开始时间 */
    private LocalDateTime registrationStart;

    /** 报名结束时间 */
    private LocalDateTime registrationEnd;

    /** 活动开始时间 */
    private LocalDateTime activityStart;

    /** 活动结束时间 */
    private LocalDateTime activityEnd;

    /** 人数上限 */
    private Integer maxParticipants;

    /** 状态：DRAFT / REGISTRATING / IN_PROGRESS / ENDED */
    private String status;
}

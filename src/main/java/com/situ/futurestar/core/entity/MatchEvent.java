package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 赛事活动实体类
 * 对应数据库表：match_event（赛事活动表）
 */
@Data
public class MatchEvent {

    /** 活动ID（主键，自增） */
    private Long id;

    /** 活动标题 */
    private String title;

    /** 类型：MATCH 比赛 / CAMP 训练营 / SELECTION 选拔 */
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

    /** 当前报名人数 */
    private Integer currentParticipants;

    /** 状态：DRAFT 草稿 / REGISTRATING 报名中 / IN_PROGRESS 进行中 / ENDED 已结束 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

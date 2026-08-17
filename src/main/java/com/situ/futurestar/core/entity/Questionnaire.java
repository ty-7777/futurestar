package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 问卷实体类
 * 对应数据库表：questionnaire（问卷表）
 */
@Data
public class Questionnaire {

    /** 问卷ID（主键，自增） */
    private Long id;

    /** 问卷标题 */
    private String title;

    /** 问卷描述 */
    private String description;

    /** 状态：DRAFT 草稿 / PUBLISHED 已发布 */
    private String status;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

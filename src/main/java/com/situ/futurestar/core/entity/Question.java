package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体类
 * 对应数据库表：question（题目表）
 */
@Data
public class Question {

    /** 题目ID（主键，自增） */
    private Long id;

    /** 问卷ID */
    private Long questionnaireId;

    /** 题目内容 */
    private String content;

    /** 类型：SINGLE 单选 / MULTIPLE 多选 / TEXT 文本 */
    private String type;

    /** 选项JSON数组 */
    private String options;

    /** 排序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

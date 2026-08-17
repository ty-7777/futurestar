package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 训练指导实体类
 * 对应数据库表：training_guidance（训练指导表）
 */
@Data
public class TrainingGuidance {

    /** 指导ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 类型：TRAINING 训练 / DIET 饮食 / RECOVERY 恢复 / DATA_SUMMARY 数据小结 */
    private String type;

    /** 指导内容 */
    private String content;

    /** 是否已读：0 未读 / 1 已读 */
    private Boolean isRead;

    /** 生成时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

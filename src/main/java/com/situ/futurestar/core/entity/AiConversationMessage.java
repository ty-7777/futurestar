package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI对话消息实体类
 * 对应数据库表：ai_conversation_message（AI对话消息表）
 */
@Data
public class AiConversationMessage {

    /** 消息ID（主键，自增） */
    private Long id;

    /** 会话ID */
    private Long sessionId;

    /** 用户ID */
    private Long userId;

    /** 角色：user 用户 / assistant 助手 */
    private String role;

    /** 消息内容 */
    private String message;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

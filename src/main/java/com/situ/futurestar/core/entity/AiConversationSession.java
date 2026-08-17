package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI会话实体类
 * 对应数据库表：ai_conversation_session（AI会话表）
 */
@Data
public class AiConversationSession {

    /** 会话ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 会话名称 */
    private String sessionName;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

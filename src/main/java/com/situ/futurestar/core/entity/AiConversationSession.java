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

    /** 会话类型：CHAT 普通对话 / ASSISTANT 智能客服 */
    private String type;

    /** 早期对话压缩摘要（滚动摘要，覆盖 summarizedMsgId 之前的消息） */
    private String summary;

    /** 已压缩到的消息ID（该 id 及之前的消息已被摘进 summary） */
    private Long summarizedMsgId;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

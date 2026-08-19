package com.situ.futurestar.core.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 会话 VO
 */
@Data
public class AiConversationSessionVO {

    /** 会话ID */
    private Long id;

    /** 会话名称 */
    private String sessionName;
}

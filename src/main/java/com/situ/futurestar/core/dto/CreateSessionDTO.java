package com.situ.futurestar.core.dto;

import lombok.Data;

/**
 * 创建 AI 会话请求 DTO
 */
@Data
public class CreateSessionDTO {

    /** 会话名称（为空时服务端自动生成） */
    private String sessionName;

    /** 会话类型：CHAT 普通对话 / ASSISTANT 智能客服（默认 CHAT） */
    private String type = "CHAT";
}

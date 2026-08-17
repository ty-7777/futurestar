package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送 AI 对话消息请求 DTO
 */
@Data
public class SendChatMessageDTO {

    /** 用户消息内容 */
    @NotBlank(message = "消息内容不能为空")
    private String content;
}

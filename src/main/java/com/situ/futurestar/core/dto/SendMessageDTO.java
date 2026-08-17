package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 推送消息请求 DTO（管理端，单条）
 */
@Data
public class SendMessageDTO {

    /** 接收用户 ID */
    @NotNull(message = "接收用户不能为空")
    private Long userId;

    /** 消息标题 */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /** 消息内容 */
    private String content;

    /** 是否同时发送短信 */
    private Boolean sendSms;
}

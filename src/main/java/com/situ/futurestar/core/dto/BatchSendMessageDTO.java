package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量推送消息请求 DTO（管理端）
 */
@Data
public class BatchSendMessageDTO {

    /** 接收用户 ID 列表 */
    @NotEmpty(message = "接收用户列表不能为空")
    private List<Long> userIds;

    /** 消息标题 */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /** 消息内容 */
    private String content;

    /** 是否同时发送短信 */
    private Boolean sendSms;
}

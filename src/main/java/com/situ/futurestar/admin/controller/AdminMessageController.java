package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.AdminMessageService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.BatchSendMessageDTO;
import com.situ.futurestar.core.dto.SendMessageDTO;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/message")
public class AdminMessageController {
    private final AdminMessageService adminMessageService;

    //消息列表
    @GetMapping
    public Result<PageResult<Message>> messageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminMessageService.messageList(pageNum, pageSize));
    }

    //推送消息（单个）
    @PostMapping("/send")
    public Result<Void> sendMessage(@Valid @RequestBody SendMessageDTO dto) {
        adminMessageService.sendMessage(dto);
        return Result.success();
    }

    //批量推送
    @PostMapping("/batch-send")
    public Result<Void> batchSendMessage(@Valid @RequestBody BatchSendMessageDTO dto) {
        adminMessageService.batchSendMessage(dto);
        return Result.success();
    }
}

package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.MessageService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/message")
public class MessageController {
    private final MessageService messageService;
    //获取消息列表
    @GetMapping()
    public Result<PageResult<Message>> getMessage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10")int pageSize,
            @RequestParam(value = "type") String type
    ){
        return Result.success(messageService.getMessage(pageNum,pageSize,type));
    }
    //根据id查看消息详情
    @GetMapping("/{id}")
    public Result<Message> getMessageById(
            @PathVariable("id") Long messageId
    ){
        return Result.success(messageService.getMessageById(messageId));
    }
    //根据消息id标记已读
    @PutMapping("/{id}/read")
    public Result<Void> updateRead(@PathVariable("id") Long messageId){
        messageService.updateRead(messageId);
        return Result.success();
    }
    //统计当前用户未读消息的数量
    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(){
        return Result.success(messageService.unreadCount());
    }
}

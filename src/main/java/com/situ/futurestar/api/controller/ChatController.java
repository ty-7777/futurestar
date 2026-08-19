package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.ChatService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.CreateSessionDTO;
import com.situ.futurestar.core.dto.SendChatMessageDTO;
import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.vo.AiConversationSessionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/chat")
public class ChatController {
    private final ChatService chatService;
    //创建会话
    @PostMapping("/session")
    public Result<AiConversationSessionVO> session(@RequestBody CreateSessionDTO createSessionDTO){
        return Result.success(chatService.session(createSessionDTO));
    }
    //查看会话列表
    @GetMapping("/session/list")
    public Result<List<AiConversationSessionVO>> sessionList(){
        return Result.success(chatService.sessionList());
    }
    //发送消息（流式）
    @PostMapping(value = "/session/{id}/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)// ① 声明 SSE
    public Flux<String> streamMessage(@PathVariable("id") Long sessionId,
                                      @Valid @RequestBody SendChatMessageDTO sendChatMessageDTO){
        return chatService.streamMessage(sessionId,sendChatMessageDTO.getContent())
                .concatWithValues("[DONE]");//② 流结束追加 [DONE]
    }
    //对话历史
    @GetMapping("/session/{id}/messages")
    public Result<List<AiConversationMessage>> messages(@PathVariable("id") Long sessionId){
        return Result.success(chatService.messages(sessionId));
    }
    //删除会话（逻辑删除）
    @DeleteMapping("/session/{id}")
    public Result<Void> deleteSession(@PathVariable("id") Long sessionId){
        chatService.deleteSession(sessionId);
        return Result.success();
    }
}

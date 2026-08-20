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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
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
    public Result<List<AiConversationSessionVO>> sessionList(@RequestParam(defaultValue = "CHAT") String type){
        return Result.success(chatService.sessionList(type));
    }
    //发送消息（流式）
    //注意：Spring MVC 下直接返回 Flux 做 SSE，流结束后连接不会关闭（前端一直转圈），
    //必须用 SseEmitter 显式 complete() 才能干净地结束响应
    @PostMapping(value = "/session/{id}/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)// ① 声明 SSE
    public SseEmitter streamMessage(@PathVariable("id") Long sessionId,
                                    @Valid @RequestBody SendChatMessageDTO sendChatMessageDTO){
        return streamToEmitter(chatService.streamMessage(sessionId, sendChatMessageDTO.getContent())
                .concatWithValues("[DONE]"));// 流结束追加 [DONE]
    }
    //AI 智能客服（带工具）
    @PostMapping(value = "/session/{id}/assistant-stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAssistant(@PathVariable("id") Long sessionId,
                                      @Valid @RequestBody SendChatMessageDTO sendChatMessageDTO){
        return streamToEmitter(chatService.streamAssistant(sessionId, sendChatMessageDTO.getContent())
                .concatWithValues("[DONE]"));
    }
    //把 Flux 桥接到 SseEmitter：每个元素按 data:<文本>\n\n 发送，流结束调用 complete() 关闭连接
    private SseEmitter streamToEmitter(Flux<String> flux) {
        SseEmitter emitter = new SseEmitter(0L);// 0 = 不超时，断开由客户端控制
        flux.subscribe(
                text -> {
                    try {
                        emitter.send(text);
                    } catch (IOException e) {
                        emitter.completeWithError(e);// 客户端断开：结束发射器
                    }
                },
                emitter::completeWithError,
                emitter::complete
        );
        return emitter;
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

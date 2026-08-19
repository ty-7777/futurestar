package com.situ.futurestar.api.service.impl;

import com.situ.futurestar.api.service.ChatService;
import com.situ.futurestar.api.service.PromptService;
import com.situ.futurestar.core.dto.CreateSessionDTO;
import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.entity.AiConversationSession;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.AiConversationMessageMapper;
import com.situ.futurestar.core.mapper.AiSessionMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.AiConversationSessionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final AiSessionMapper aiSessionMapper;
    private final AiConversationMessageMapper aiConversationMessageMapper;
    private final ChatClient chatClient;
    private final PromptService promptService;
    @Override
    public AiConversationSessionVO session(CreateSessionDTO createSessionDTO) {
        AiConversationSession session = new AiConversationSession();
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        session.setSessionName(createSessionDTO.getSessionName());
        session.setUserId(userId);
        aiSessionMapper.createSession(session,userId);
        AiConversationSessionVO vo =new AiConversationSessionVO();
        vo.setSessionName(session.getSessionName());
        vo.setId(session.getId());
        return vo;
    }

    @Override
    public List<AiConversationSessionVO> sessionList() {
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        return aiSessionMapper.sessionList(userId);
    }

    @Override
    public Flux<String> streamMessage(Long sessionId, String content) {
        // ① 加载会话（本来就要做：校验归属）
        Long userId = SecurityUtil.getCurrentUserId();
        AiConversationSession session = aiSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BizException("会话不存在或无权限");
        }

        // ② 命名：用内存里已查出的值判断，只有首条消息（name为空）才 UPDATE
        if (session.getSessionName() == null) {
            String name = content.length() > 20 ? content.substring(0, 20) + "…" : content;
            aiSessionMapper.setSessionName(sessionId, name);
        }
        //先加载会话历史,让AI携带最近十轮的上下文
        List<AiConversationMessage> list = aiConversationMessageMapper.loadHistory(sessionId);
        //把历史上下文反转成正序
        List<Message> history = list.stream().map(
                m -> (Message)("user".equals(m.getRole())
                        ? new UserMessage(m.getMessage())
                        : new AssistantMessage(m.getMessage()))
        ).toList();
        Collections.reverse(history);
        //先把用户发的消息存入消息表
        saveMessage(sessionId,userId,"user",content);
        //建一个StringBuilder来存Ai回的消息
        StringBuilder sb =new StringBuilder();
        return chatClient.prompt()
                .system(promptService.get("ai_chat_system_prompt"))
                .messages(history)
                .user(content)
                .stream()
                .content()
                .doOnNext(sb::append)
                .doOnComplete(()->saveMessage(sessionId,userId,"assistant",sb.toString()))
                .doOnError(e->saveMessage(sessionId,userId,"assistant","[生成失败] " + e.getMessage()))
                .doOnCancel(()->{
                    // 客户端中途断开（SSE 取消）：保存已接收片段，不丢数据（设计文档 5.4）
                    if (sb.length() > 0) {
                        saveMessage(sessionId,userId,"assistant",sb.toString());
                    }
                });


    }

    @Override
    public List<AiConversationMessage> messages(Long sessionId) {
        Long userId = SecurityUtil.getCurrentUserId();
        AiConversationSession session = aiSessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new BizException("会话不存在或无权限");
        }
        return aiConversationMessageMapper.listBySession(sessionId);
    }

    @Override
    public void deleteSession(Long sessionId) {
        if (sessionId == null || sessionId < 0) {
            throw new BizException("会话id不合法");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        int updated = aiSessionMapper.deleteSession(sessionId, userId);
        if (updated != 1) {
            throw new BizException("会话不存在或无权限");
        }
    }
    //每次对话保存双方消息
    private void saveMessage(Long sessionId, Long userId, String role, String content){
        AiConversationMessage message =new AiConversationMessage();
        message.setSessionId(sessionId);
        message.setUserId(userId);
        message.setRole(role);
        message.setMessage(content);
        aiConversationMessageMapper.insert(message);
    }
}

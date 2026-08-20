package com.situ.futurestar.api.service.impl;

import com.situ.futurestar.api.service.ChatService;
import com.situ.futurestar.api.service.PromptService;
import com.situ.futurestar.api.tools.CourseTools;
import com.situ.futurestar.api.tools.EventTools;
import com.situ.futurestar.core.dto.CreateSessionDTO;
import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.entity.AiConversationSession;
import com.situ.futurestar.core.entity.User;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final AiSessionMapper aiSessionMapper;
    private final AiConversationMessageMapper aiConversationMessageMapper;
    private final ChatClient chatClient;
    private final PromptService promptService;
    private final CourseTools courseTools;
    private final EventTools eventTools;

    @Override
    public AiConversationSessionVO session(CreateSessionDTO createSessionDTO) {
        AiConversationSession session = new AiConversationSession();
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        session.setSessionName(createSessionDTO.getSessionName());
        session.setType(createSessionDTO.getType());
        session.setUserId(userId);
        aiSessionMapper.createSession(session,userId);
        AiConversationSessionVO vo =new AiConversationSessionVO();
        vo.setSessionName(session.getSessionName());
        vo.setId(session.getId());
        return vo;
    }

    @Override
    public List<AiConversationSessionVO> sessionList(String type) {
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        return aiSessionMapper.sessionList(userId, type);
    }

    @Override
    public Flux<String> streamMessage(Long sessionId, String content) {
        // 普通 AI 对话：纯聊天，不注册工具
        return doStream(sessionId, content, "ai_chat_system_prompt", false);
    }

    @Override
    public Flux<String> streamAssistant(Long sessionId, String content) {
        // AI 智能客服：注册课程/赛事工具，用独立提示词
        return doStream(sessionId, content, "ai_assistant_system_prompt", true);
    }

    private Flux<String> doStream(Long sessionId, String content, String promptKey, boolean withTools) {
        // ① 加载会话（本来就要做：校验归属）；主线程取用户，异步工具线程无 SecurityContext
        User user = SecurityUtil.getCurrentUser();
        Long userId = user.getId();
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
        //把历史上下文反转成正序（Stream.toList() 返回不可变列表，reverse 会抛异常，需先包一层 ArrayList）
        List<Message> history = new ArrayList<>(list.stream().map(
                m -> (Message)("user".equals(m.getRole())
                        ? new UserMessage(m.getMessage())
                        : new AssistantMessage(m.getMessage()))
        ).toList());
        Collections.reverse(history);
        //先把用户发的消息存入消息表
        saveMessage(sessionId,userId,"user",content);
        //建一个StringBuilder来存Ai回的消息
        StringBuilder sb =new StringBuilder();
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                .system(promptService.get(promptKey))
                .messages(history)
                .user(content);
        if (withTools) {
            spec = spec.tools(courseTools, eventTools)
                    .toolContext(Map.of(CourseTools.USER_KEY, user));
        }
        return spec.stream()
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

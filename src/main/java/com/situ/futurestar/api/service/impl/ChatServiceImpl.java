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
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
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
        //加载压缩边界之后的全部消息（窗口 + 待压缩区都进上下文，待压缩区被压缩阈值20封顶，避免中间地带临时失忆）
        //拿到上次压缩到的消息id
        Long boundary = session.getSummarizedMsgId() == null ? 0L : session.getSummarizedMsgId();
        //从上次压缩的消息往后取40条，不够40就有多少取多少
        List<AiConversationMessage> list = aiConversationMessageMapper.loadAfter(sessionId, boundary);
        //把历史上下文反转成正序（Stream.toList() 返回不可变列表，reverse 会抛异常，需先包一层 ArrayList）
        List<Message> history = new ArrayList<>(list.stream().map(
                m -> (Message)("user".equals(m.getRole())
                        ? new UserMessage(m.getMessage())
                        : new AssistantMessage(m.getMessage()))
        ).toList());
        Collections.reverse(history);
        //有历史摘要时，以独立 SystemMessage 携带（模板提示词是全会话共用的，摘要只属于当前会话）
        if (session.getSummary() != null && !session.getSummary().isBlank()) {
            history.add(0, new SystemMessage("以下是之前对话的摘要，请结合它理解上下文：" + session.getSummary()));
        }
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
                .doOnComplete(()->{
                    saveMessage(sessionId,userId,"assistant",sb.toString());
                    //回复完成后异步压缩上下文：不阻塞本次响应，摘要给下次请求用
                    CompletableFuture.runAsync(() -> compressIfNeeded(session));
                })
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

    /** 待压缩区消息数超过该阈值才触发压缩 */
    private static final int COMPRESS_THRESHOLD = 20;

    /**
     * 滚动压缩会话上下文：把「已压缩边界 ~ 保留窗口之前」的消息 + 旧摘要交给 AI 生成新摘要回写。
     * 压缩范围与 loadHistory 的 LIMIT 20 窗口对齐，保证被压缩的消息不会和原文窗口重复携带。
     * 异步执行（无 SecurityContext，但压缩不需要登录态），失败只记日志，不影响主流程。
     */
    private void compressIfNeeded(AiConversationSession session) {
        try {
            //保留窗口（与 loadHistory 一致）：不足 20 条说明历史全在窗口里，无需压缩；
            //loadHistory 按 id 倒序返回，最后一条就是窗口内最小 id，即压缩上界（不含）
            List<AiConversationMessage> window = aiConversationMessageMapper.loadHistory(session.getId());
            if (window.size() < 20) {//如果会话记录一共不到20条，直接返回
                return;
            }
            Long fromId = session.getSummarizedMsgId() == null ? 0L : session.getSummarizedMsgId();//如果上次压缩到的消息id为空，设为0
            Long toId = window.get(window.size() - 1).getId() - 1;//最新的20条里，最小id的，是保留窗口里最早的一条
            if (toId <= fromId) {//如果保留窗口里最早的一条消息id小于等于上次压缩到的消息id，说明没有需要压缩的消息
                return;
            }
            //走到这说明上次压缩的id和这次压缩的id之间有需要压缩的消息即压缩窗口
            List<AiConversationMessage> pending = aiConversationMessageMapper.selectPendingSummary(session.getId(), fromId, toId);
            //判断压缩窗口的消息数是否超过阈值
            if (pending.size() < COMPRESS_THRESHOLD) {
                //压缩窗口不到20条，直接返回，无需压缩
                return;
            }
            //拼压缩输入：旧摘要 + 待压缩区消息（id 升序，符合对话顺序）
            StringBuilder input = new StringBuilder();
            if (session.getSummary() != null && !session.getSummary().isBlank()) {
                input.append("之前的对话摘要：\n").append(session.getSummary()).append("\n\n新增的对话：\n");
            }
            for (AiConversationMessage m : pending) {
                input.append("user".equals(m.getRole()) ? "用户：" : "助手：").append(m.getMessage()).append('\n');
            }
            //压缩是纯总结任务：不带 tools、不带历史，同步调用即可（已在异步线程）
            String newSummary = chatClient.prompt()
                    .system(promptService.get("ai_summary_prompt"))
                    .user(input.toString())
                    .call()
                    .content();
            if (newSummary == null || newSummary.isBlank()) {
                return;
            }
            //乐观锁：WHERE 带旧 summarizedMsgId，返回 0 说明并发压缩已先行，静默放弃
            aiSessionMapper.updateSummary(session.getId(), newSummary, toId, fromId);
        } catch (Exception e) {
            log.warn("会话上下文压缩失败 sessionId={}: {}", session.getId(), e.getMessage());
        }
    }
}

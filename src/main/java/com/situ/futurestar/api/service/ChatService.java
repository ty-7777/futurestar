package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.CreateSessionDTO;
import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.vo.AiConversationSessionVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    AiConversationSessionVO session(CreateSessionDTO createSessionDTO);

    List<AiConversationSessionVO> sessionList(String type);

    Flux<String> streamMessage(Long sessionId, String content);

    /** AI 智能客服：带 Function Calling 工具，用独立提示词 */
    Flux<String> streamAssistant(Long sessionId, String content);

    List<AiConversationMessage> messages(Long sessionId);

    void deleteSession(Long sessionId);
}

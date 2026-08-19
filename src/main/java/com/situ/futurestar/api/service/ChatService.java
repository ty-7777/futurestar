package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.CreateSessionDTO;
import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.vo.AiConversationSessionVO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ChatService {
    AiConversationSessionVO session(CreateSessionDTO createSessionDTO);

    List<AiConversationSessionVO> sessionList();

    Flux<String> streamMessage(Long sessionId, String content);

    List<AiConversationMessage> messages(Long sessionId);

    void deleteSession(Long sessionId);
}

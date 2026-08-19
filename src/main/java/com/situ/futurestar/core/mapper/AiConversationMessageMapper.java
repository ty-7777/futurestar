package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AiConversationMessageMapper {

    // 清理保留期之前的消息（定时任务）
    int deleteExpired(LocalDateTime before);

    List<AiConversationMessage> loadHistory(Long sessionId);

    void insert(AiConversationMessage message);

    List<AiConversationMessage> listBySession(Long sessionId);
}

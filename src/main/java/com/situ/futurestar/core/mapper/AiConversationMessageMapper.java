package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.AiConversationMessage;
import com.situ.futurestar.core.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AiConversationMessageMapper {

    // 清理保留期之前的消息（定时任务）
    int deleteExpired(LocalDateTime before);

    List<AiConversationMessage> loadHistory(Long sessionId);

    // 加载压缩边界之后的全部消息（窗口 + 待压缩区），用于拼装对话上下文，避免待压缩区消息漏传
    List<AiConversationMessage> loadAfter(@Param("sessionId") Long sessionId,
                                          @Param("boundaryId") Long boundaryId);

    void insert(AiConversationMessage message);

    List<AiConversationMessage> listBySession(Long sessionId);

    // 查待压缩区消息：id 在 (fromId, toId] 之间，正序返回
    List<AiConversationMessage> selectPendingSummary(@Param("sessionId") Long sessionId,
                                                     @Param("fromId") Long fromId,
                                                     @Param("toId") Long toId);
}

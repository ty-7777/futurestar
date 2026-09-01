package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.AiConversationSession;
import com.situ.futurestar.core.vo.AiConversationSessionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiSessionMapper {
    void createSession(@Param("session") AiConversationSession session , @Param("userId") Long userId);
    void setSessionName(@Param("sessionId") Long sessionId, @Param("sessionName") String sessionName);

    List<AiConversationSessionVO> sessionList(@Param("userId") Long userId, @Param("type") String type);

    AiConversationSession selectById(Long sessionId);

    int deleteSession(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    //回写压缩摘要（乐观锁：WHERE 带旧 summarizedMsgId，并发压缩只有一个能成功）
    int updateSummary(@Param("sessionId") Long sessionId,
                      @Param("summary") String summary,
                      @Param("summarizedMsgId") Long summarizedMsgId,
                      @Param("oldSummarizedMsgId") Long oldSummarizedMsgId);
}

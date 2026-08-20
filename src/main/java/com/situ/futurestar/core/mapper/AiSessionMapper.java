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
}

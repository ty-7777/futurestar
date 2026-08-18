package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    List<Message> messageList(@Param("userId") Long userId,@Param("type") String type);

    Message getMessageById(@Param("messageId") Long messageId ,@Param("userId") Long userId);

    int updateRead(@Param("messageId") Long messageId ,@Param("userId") Long userId);

    Integer unreadCount(Long userId);
}

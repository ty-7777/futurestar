package com.situ.futurestar.api.service;

import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.vo.PageResult;

public interface MessageService {
    PageResult<Message> getMessage(int pageNum, int pageSize, String type);

    Message getMessageById(Long messageId);

    void updateRead(Long messageId);

    Integer unreadCount();

}

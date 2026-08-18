package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.dto.BatchSendMessageDTO;
import com.situ.futurestar.core.dto.SendMessageDTO;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.vo.PageResult;

public interface AdminMessageService {

    PageResult<Message> messageList(int pageNum, int pageSize);

    void sendMessage(SendMessageDTO dto);

    void batchSendMessage(BatchSendMessageDTO dto);
}

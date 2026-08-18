package com.situ.futurestar.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.admin.service.AdminMessageService;
import com.situ.futurestar.core.dto.BatchSendMessageDTO;
import com.situ.futurestar.core.dto.SendMessageDTO;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.MessageMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMessageServiceImpl implements AdminMessageService {
    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<Message> messageList(int pageNum, int pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Message> list = messageMapper.listAll();
        PageInfo<Message> pageInfo = new PageInfo<>(list);
        PageResult<Message> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public void sendMessage(SendMessageDTO dto) {
        User user = userMapper.selectById(dto.getUserId());
        if (user == null) {
            throw new BizException("接收用户不存在");
        }
        insertMessage(dto.getUserId(), dto.getTitle(), dto.getContent());
        // TODO: dto.getSendSms() 短信功能未实现，暂忽略（只存站内消息）
    }

    @Override
    public void batchSendMessage(BatchSendMessageDTO dto) {
        for (Long userId : dto.getUserIds()) {
            insertMessage(userId, dto.getTitle(), dto.getContent());
        }
        // TODO: dto.getSendSms() 短信功能未实现，暂忽略
    }

    private void insertMessage(Long userId, String title, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType("SYSTEM");
        message.setIsRead(false);
        messageMapper.insert(message);
    }
}

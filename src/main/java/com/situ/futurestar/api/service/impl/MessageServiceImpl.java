package com.situ.futurestar.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.MessageService;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.MessageMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;
    @Override
    public PageResult<Message> getMessage(int pageNum, int pageSize, String type) {
        if(pageNum<0||pageSize<=0){
            throw new BizException("分页参数不合法");
        }
        if(type==null||type.isBlank()){
            throw new BizException("类型不能为空");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        PageHelper.startPage(pageNum,pageSize);
        List<Message> list = messageMapper.messageList(userId,type);
        PageInfo<Message> pageInfo =new PageInfo<>(list);
        PageResult<Message> result =new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setList(list);
        if(list.isEmpty()){
            result.setTotal(0);
            result.setPages(0);
            return result;
        }
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        return result;
    }

    @Override
    public Message getMessageById(Long messageId) {
        if(messageId==null||messageId<0){
            throw new BizException("id不合法");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        Message message = messageMapper.getMessageById(messageId,userId);
        if(message==null){
            throw new BizException("消息不存在");
        }
        return message;
    }

    @Override
    public void updateRead(Long messageId) {
        if(messageId==null||messageId<0){
            throw new BizException("id不合法");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        int updated = messageMapper.updateRead(messageId,userId);
        if(updated!=1){
            throw new BizException("消息不存在");
        }
    }

    @Override
    public Integer unreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        Integer count = messageMapper.unreadCount(userId);
        return count;
    }
}

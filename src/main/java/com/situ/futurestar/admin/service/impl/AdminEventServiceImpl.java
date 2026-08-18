package com.situ.futurestar.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.admin.service.AdminEventService;
import com.situ.futurestar.core.dto.MatchEventDTO;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.EventMapper;
import com.situ.futurestar.core.vo.EventRegistrationVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventMapper eventMapper;

    @Override
    public PageResult<MatchEvent> eventList(int pageNum, int pageSize, String status) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<MatchEvent> list = eventMapper.listAllEvents(status);
        PageInfo<MatchEvent> pageInfo = new PageInfo<>(list);
        PageResult<MatchEvent> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public void createEvent(MatchEventDTO dto) {
        MatchEvent event = new MatchEvent();
        BeanUtils.copyProperties(dto, event);
        if (event.getStatus() == null) {
            event.setStatus("DRAFT");
        }
        eventMapper.insertEvent(event);
    }

    @Override
    public void updateEvent(Long id, MatchEventDTO dto) {
        if (id == null || id < 0) {
            throw new BizException("活动id不合法");
        }
        MatchEvent event = new MatchEvent();
        BeanUtils.copyProperties(dto, event);
        event.setId(id);
        int updated = eventMapper.updateEvent(event);
        if (updated != 1) {
            throw new BizException("活动不存在");
        }
    }

    @Override
    public void deleteEvent(Long id) {
        if (id == null || id < 0) {
            throw new BizException("活动id不合法");
        }
        int updated = eventMapper.deleteEvent(id);
        if (updated != 1) {
            throw new BizException("活动不存在");
        }
    }

    @Override
    public PageResult<EventRegistrationVO> registrationList(int pageNum, int pageSize, Long eventId) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        if (eventId == null || eventId < 0) {
            throw new BizException("活动id不合法");
        }
        if (eventMapper.selectById(eventId) == null) {
            throw new BizException("活动不存在");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<EventRegistrationVO> list = eventMapper.listRegistrations(eventId);
        PageInfo<EventRegistrationVO> pageInfo = new PageInfo<>(list);
        PageResult<EventRegistrationVO> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }
}

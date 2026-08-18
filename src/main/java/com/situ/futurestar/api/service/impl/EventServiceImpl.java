package com.situ.futurestar.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.EventService;
import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.entity.EventRegistration;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.EventMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    @Override
    public PageResult<MatchEvent> eventList(int pageNum, int pageSize, String type) {
        if(pageNum<0||pageSize<=0){
            throw  new BizException("分页参数不合法");
        }
        if(type==null||type.isBlank()){
            throw new BizException("类型不合法");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<MatchEvent> list = eventMapper.eventList(type);
        PageInfo<MatchEvent> pageInfo = new PageInfo<>(list);
        PageResult<MatchEvent> result =new PageResult<>();
        if(list.isEmpty()){
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setTotal(0);
            result.setPages(0);
            result.setList(list);
            return result;
        }
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public MatchEvent eventById(Long id) {
        if(id==null||id<0){
            throw new BizException("id不合法");
        }
        MatchEvent matchEvent = eventMapper.selectById(id);
        if(matchEvent==null){
            throw new BizException("活动不存在");
        }
        return matchEvent;
    }

    @Override
    @Transactional
    public void signUp(Long eventId) {
        if(eventId==null||eventId<0){
            throw new BizException("活动id不合法");
        }
        MatchEvent matchEvent = eventMapper.selectById(eventId);
        if(matchEvent==null){
            throw new BizException("活动不存在");
        }
        if(!"REGISTRATING".equals(matchEvent.getStatus())){
            throw new BizException("该活动状态未在报名中，报名失败");
        }
        //先查看当前活动人数满没满，不满就直接加1看看成不成功
       int updated =  eventMapper.plus(eventId);
        if(updated!=1){
            throw new BizException(ErrorCode.CONFLICT,"活动报名人数已满，报名失败");
        }
        //走到这里说明满足报名条件
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        //新建报名活动表
        EventRegistration registration =new EventRegistration();
        registration.setEventId(eventId);
        registration.setUserId(userId);
        try {
            eventMapper.signUp(registration);
            //报名表根据userId和eventId建立了联合唯一索引来防止重复报名
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.CONFLICT,"请勿重复报名");
        }
    }

    @Override
    public PageResult<MatchEvent> myEvent(int pageNum, int pageSize) {
        if(pageNum<0||pageSize<=0){
            throw  new BizException("分页参数不合法");
        }
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        PageHelper.startPage(pageNum,pageSize);
        List<MatchEvent> list = eventMapper.myEvent(userId);
        PageInfo<MatchEvent> pageInfo =new PageInfo<>(list);
        PageResult<MatchEvent> result =new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        if(list.isEmpty()){
           result.setPages(0);
           result.setTotal(0);
           result.setList(list);
           return result;
        }
        result.setPages(pageInfo.getPages());
        result.setTotal(pageInfo.getTotal());
        result.setList(list);
        return result;
    }

    @Override
    public String checkinStatus(Long eventId) {
        if(eventId==null||eventId<0){
            throw new BizException("活动id不合法");
        }
        //获取当前用户id
        Long userId = SecurityUtil.getCurrentUserId();
        String status = eventMapper.checkinStatus(eventId,userId);
        if(status==null){
            throw new BizException("活动报名表不存在");
        }
        return status;
    }

    @Override
    @Transactional
    public void checkin(Long eventId) {
        if(eventId==null||eventId<0){
            throw new BizException("活动id不合法");
        }
        //查看是否已报名
        Long userId = SecurityUtil.getCurrentUserId();
        String status = eventMapper.checkinStatus(eventId,userId);
        if(status==null){
            throw new BizException("活动报名表不存在");
        }
        if("CHECKED_IN".equals(status)){
            throw new BizException("已签到，请勿重复签到");
        }
        String eventStatus = eventMapper.checkin(eventId,userId);
         if(eventStatus==null){
             throw new BizException("未报名，无法签到");
         }
        //校验活动是否在进行中
        if(!"IN_PROGRESS".equals(eventStatus)){
            throw new BizException("活动未在进行中，无法签到");
        }
        //走到这里说明具备签到条件
        //更新签到状态
        int updated = eventMapper.updateEventStatus(eventId, userId);
        if(updated!=1){
            throw new BizException("已签到，请勿重复签到");
        }
        //签到成功，奖励用户50积分
        userMapper.updatePoints(userId,50);
    }
}

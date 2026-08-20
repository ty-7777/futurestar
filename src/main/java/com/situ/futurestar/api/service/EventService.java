package com.situ.futurestar.api.service;

import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.vo.PageResult;

public interface EventService {
    PageResult<MatchEvent> eventList(int pageNum, int pageSize, String type, String keyword);

    MatchEvent eventById(Long id);

    void signUp(Long eventId);

    //以下为 AI 客服工具调用专用重载（异步线程无 SecurityContext，显式传用户）
    void signUp(Long eventId, User user);

    PageResult<MatchEvent> myEvent(int pageNum, int pageSize);

    PageResult<MatchEvent> myEvent(int pageNum, int pageSize, User user);

    String checkinStatus(Long eventId);

    void checkin(Long eventId);
}

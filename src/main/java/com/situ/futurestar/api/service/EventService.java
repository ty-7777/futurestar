package com.situ.futurestar.api.service;

import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.vo.PageResult;

public interface EventService {
    PageResult<MatchEvent> eventList(int pageNum, int pageSize, String type);

    MatchEvent eventById(Long id);

    void signUp(Long eventId);

    PageResult<MatchEvent> myEvent(int pageNum, int pageSize);

    String checkinStatus(Long eventId);

    void checkin(Long eventId);
}

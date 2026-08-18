package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.dto.MatchEventDTO;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.vo.EventRegistrationVO;
import com.situ.futurestar.core.vo.PageResult;

public interface AdminEventService {

    PageResult<MatchEvent> eventList(int pageNum, int pageSize, String status);

    void createEvent(MatchEventDTO dto);

    void updateEvent(Long id, MatchEventDTO dto);

    void deleteEvent(Long id);

    PageResult<EventRegistrationVO> registrationList(int pageNum, int pageSize, Long eventId);
}

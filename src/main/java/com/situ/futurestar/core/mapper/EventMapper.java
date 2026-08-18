package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.EventRegistration;
import com.situ.futurestar.core.entity.MatchEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {
    List<MatchEvent> eventList(String type);

    MatchEvent selectById(Long id);

    int plus(Long eventId);

    void signUp(EventRegistration registration);

    List<MatchEvent> myEvent(Long userId);

    String checkinStatus(@Param("eventId") Long eventId,@Param("userId") Long userId);

    String checkin(@Param("eventId")Long eventId, @Param("userId")Long userId);

    int updateEventStatus(@Param("eventId")Long eventId, @Param("userId") Long userId);
}

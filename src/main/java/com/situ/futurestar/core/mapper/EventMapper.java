package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.EventRegistration;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.vo.EventRegistrationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface EventMapper {
    // ---------- 会员端 ----------
    List<MatchEvent> eventList(@Param("type") String type, @Param("keyword") String keyword);

    MatchEvent selectById(Long id);

    int plus(Long eventId);

    void signUp(EventRegistration registration);

    List<MatchEvent> myEvent(Long userId);

    String checkinStatus(@Param("eventId") Long eventId,@Param("userId") Long userId);

    String checkin(@Param("eventId")Long eventId, @Param("userId")Long userId);

    int updateEventStatus(@Param("eventId")Long eventId, @Param("userId") Long userId);

    // ---------- 管理端 ----------
    List<MatchEvent> listAllEvents(@Param("status") String status);

    int insertEvent(MatchEvent event);

    int updateEvent(MatchEvent event);

    int deleteEvent(Long id);

    List<EventRegistrationVO> listRegistrations(@Param("eventId") Long eventId);

    // ---------- 定时任务 ----------
    int updateStatusToRegistering(LocalDateTime now);

    int updateStatusToInProgress(LocalDateTime now);

    int updateStatusToEnded(LocalDateTime now);
}

package com.situ.futurestar.core.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {
    int countPlayers();

    int countTodayNewPlayers();

    int countTodayCourseAppointments();

    int countTodayEventRegistrations();

    int countPendingAppointments();
}

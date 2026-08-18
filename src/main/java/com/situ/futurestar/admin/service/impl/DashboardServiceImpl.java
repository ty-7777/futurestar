package com.situ.futurestar.admin.service.impl;

import com.situ.futurestar.admin.service.DashboardService;
import com.situ.futurestar.core.mapper.DashboardMapper;
import com.situ.futurestar.core.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final DashboardMapper dashboardMapper;

    @Override
    public DashboardVO overview() {
        DashboardVO vo = new DashboardVO();
        vo.setPlayerTotal(dashboardMapper.countPlayers());
        vo.setTodayNewPlayers(dashboardMapper.countTodayNewPlayers());
        vo.setTodayCourseAppointments(dashboardMapper.countTodayCourseAppointments());
        vo.setTodayEventRegistrations(dashboardMapper.countTodayEventRegistrations());
        vo.setPendingAppointments(dashboardMapper.countPendingAppointments());
        return vo;
    }
}

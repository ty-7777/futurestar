package com.situ.futurestar.core.vo;

import lombok.Data;

/**
 * 管理端仪表盘数据概览 VO
 */
@Data
public class DashboardVO {

    /** 球员总数 */
    private Integer playerTotal;

    /** 今日新增球员 */
    private Integer todayNewPlayers;

    /** 今日课程预约数 */
    private Integer todayCourseAppointments;

    /** 今日赛事报名数 */
    private Integer todayEventRegistrations;

    /** 待确认预约数 */
    private Integer pendingAppointments;
}

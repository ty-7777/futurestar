package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.DashboardService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.vo.DashboardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    //数据概览
    @GetMapping
    public Result<DashboardVO> overview() {
        return Result.success(dashboardService.overview());
    }
}

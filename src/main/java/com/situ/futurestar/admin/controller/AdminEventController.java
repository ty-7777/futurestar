package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.AdminEventService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.MatchEventDTO;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.vo.EventRegistrationVO;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/event")
public class AdminEventController {
    private final AdminEventService adminEventService;

    //活动列表
    @GetMapping
    public Result<PageResult<MatchEvent>> eventList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status
    ) {
        return Result.success(adminEventService.eventList(pageNum, pageSize, status));
    }

    //新增活动
    @PostMapping
    public Result<Void> createEvent(@Valid @RequestBody MatchEventDTO dto) {
        adminEventService.createEvent(dto);
        return Result.success();
    }

    //修改活动
    @PutMapping("/{id}")
    public Result<Void> updateEvent(@PathVariable("id") Long id, @RequestBody MatchEventDTO dto) {
        adminEventService.updateEvent(id, dto);
        return Result.success();
    }

    //删除活动（逻辑删除）
    @DeleteMapping("/{id}")
    public Result<Void> deleteEvent(@PathVariable("id") Long id) {
        adminEventService.deleteEvent(id);
        return Result.success();
    }

    //报名列表
    @GetMapping("/{id}/registrations")
    public Result<PageResult<EventRegistrationVO>> registrationList(
            @PathVariable("id") Long eventId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminEventService.registrationList(pageNum, pageSize, eventId));
    }
}

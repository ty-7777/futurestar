package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.EventService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/event")
public class EventController {
    private final EventService eventService;
    //获取活动列表
    @GetMapping()
    public Result<PageResult<MatchEvent>> eventList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword
    ){
        return Result.success(eventService.eventList(pageNum,pageSize,type,keyword));
    }
    //查看活动详情
    @GetMapping("/{id}")
    public Result<MatchEvent> eventById(@PathVariable("id") Long id){
        return Result.success(eventService.eventById(id));
    }
    //报名
    @PostMapping("/{id}/register")
    public Result<Void> signUp(@PathVariable("id") Long eventId){
        eventService.signUp(eventId);
        return Result.success();
    }
    //查看我的活动
    @GetMapping("/my")
    public Result<PageResult<MatchEvent>> myEvent (
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        return Result.success(eventService.myEvent(pageNum,pageSize));
    }
    //查看活动的签到状态
    @GetMapping("/{id}/checkin-status")
    public Result<String> checkinStatus(@PathVariable("id") Long eventId){
        return  Result.success(eventService.checkinStatus(eventId));
    }
    //活动签到
    @PostMapping("/{id}/checkin")
    public Result<Void> checkin(@PathVariable("id") Long eventId){
        eventService.checkin(eventId);
        return Result.success();
    }
}

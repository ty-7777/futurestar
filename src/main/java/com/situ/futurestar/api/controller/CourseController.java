package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.CourseService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.CreateAppointmentDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.CourseSlotVO;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/course")
public class CourseController {
    private final CourseService courseService;
    //获取课程套餐列表
    @GetMapping("/packages")
    public Result<PageResult<CoursePackage>> packagesList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10")int pageSize,
            @RequestParam(required = false) String keyword
    ){
        return Result.success(courseService.packagesList(pageNum,pageSize,keyword));
    }
    //根据套餐id获取套餐详情
    @GetMapping("/packages/{id}")
    public Result<CoursePackage> getPackageById(
            @PathVariable("id") Long id
    ){
        return Result.success(courseService.getPackageById(id));
    }
    //查询该日的可预约时间段
    @GetMapping("/packages/{id}/slots")
    public Result<List<CourseSlotVO>> getSlots(
            @PathVariable("id") Long id,
            @RequestParam("date")LocalDate date
            ){
        return Result.success(courseService.getSlots(id,date));
    }
    //提交预约
    @PostMapping("/appointment")
    public Result<Void> submitAppointment(@Valid @RequestBody CreateAppointmentDTO appointmentDTO){
        courseService.submitAppointment(appointmentDTO);
        return Result.success();
    }
    //我的预约
    @GetMapping("/appointment/list")
    public Result<PageResult<CourseAppointmentVO>> appointmentList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "PENDING") String status
    ){
        return Result.success(courseService.myAppointmentList(pageNum,pageSize,status));
    }
    //根据预约id查看报告
    @GetMapping("/appointment/{id}/report")
    public Result<String> report(@PathVariable("id") Long id){
        return Result.success(courseService.report(id));
    }
    //取消预约
    @PostMapping("/appointment/{id}/cancel")
    public Result<Void> cancel(@PathVariable("id") Long id){
        courseService.cancel(id);
        return Result.success();
    }
}

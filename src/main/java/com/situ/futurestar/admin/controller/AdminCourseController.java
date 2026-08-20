package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.AdminCourseService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.BatchSlotDTO;
import com.situ.futurestar.core.dto.CoursePackageDTO;
import com.situ.futurestar.core.dto.UpdateSlotDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/course")
public class AdminCourseController {
    private final AdminCourseService adminCourseService;

    //套餐列表
    @GetMapping("/packages")
    public Result<PageResult<CoursePackage>> packageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminCourseService.packageList(pageNum, pageSize));
    }

    //新增套餐
    @PostMapping("/packages")
    public Result<Void> createPackage(@Valid @RequestBody CoursePackageDTO dto) {
        adminCourseService.createPackage(dto);
        return Result.success();
    }

    //修改套餐
    @PutMapping("/packages/{id}")
    public Result<Void> updatePackage(@PathVariable("id") Long id, @RequestBody CoursePackageDTO dto) {
        adminCourseService.updatePackage(id, dto);
        return Result.success();
    }

    //删除套餐（逻辑删除）
    @DeleteMapping("/packages/{id}")
    public Result<Void> deletePackage(@PathVariable("id") Long id) {
        adminCourseService.deletePackage(id);
        return Result.success();
    }

    //批量生成时段
    @PostMapping("/packages/{id}/slots/batch")
    public Result<Void> batchCreateSlots(@PathVariable("id") Long packageId, @Valid @RequestBody BatchSlotDTO dto) {
        adminCourseService.batchCreateSlots(packageId, dto);
        return Result.success();
    }

    //时段列表
    @GetMapping("/slots")
    public Result<List<CourseSlot>> slotList(
            @RequestParam Long packageId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return Result.success(adminCourseService.slotList(packageId, date));
    }

    //修改时段（最大人数/状态）
    @PutMapping("/slots/{id}")
    public Result<Void> updateSlot(@PathVariable("id") Long id, @RequestBody UpdateSlotDTO dto) {
        adminCourseService.updateSlot(id, dto);
        return Result.success();
    }

    //预约管理列表
    @GetMapping("/appointments")
    public Result<PageResult<CourseAppointmentVO>> appointmentList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long packageId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status
    ) {
        return Result.success(adminCourseService.appointmentList(pageNum, pageSize, packageId, date, status));
    }

    //确认预约（管理员审核通过）
    @PutMapping("/appointments/{id}/confirm")
    public Result<Void> confirmAppointment(@PathVariable("id") Long id) {
        adminCourseService.confirmAppointment(id);
        return Result.success();
    }

    //拒绝预约（管理员拒绝，退还积分并释放名额）
    @PutMapping("/appointments/{id}/reject")
    public Result<Void> rejectAppointment(@PathVariable("id") Long id) {
        adminCourseService.rejectAppointment(id);
        return Result.success();
    }

    //上传报告
    @PostMapping("/appointments/{id}/report")
    public Result<String> uploadReport(@PathVariable("id") Long appointmentId,
                                       @RequestParam("file") MultipartFile file) {
        return Result.success(adminCourseService.uploadReport(appointmentId, file));
    }
}

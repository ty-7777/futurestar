package com.situ.futurestar.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.admin.service.AdminCourseService;
import com.situ.futurestar.core.dto.BatchSlotDTO;
import com.situ.futurestar.core.dto.CoursePackageDTO;
import com.situ.futurestar.core.dto.UpdateSlotDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.CourseMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCourseServiceImpl implements AdminCourseService {
    private static final String REPORT_DIR = "data/upload/report/";
    private static final long MAX_REPORT_SIZE = 20L * 1024 * 1024; // 20MB

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<CoursePackage> packageList(int pageNum, int pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CoursePackage> list = courseMapper.listAll();
        PageInfo<CoursePackage> pageInfo = new PageInfo<>(list);
        PageResult<CoursePackage> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public void createPackage(CoursePackageDTO dto) {
        CoursePackage pkg = new CoursePackage();
        BeanUtils.copyProperties(dto, pkg);
        if (pkg.getStatus() == null) {
            pkg.setStatus("ENABLED");
        }
        courseMapper.insertPackage(pkg);
    }

    @Override
    public void updatePackage(Long id, CoursePackageDTO dto) {
        if (id == null || id < 0) {
            throw new BizException("套餐id不合法");
        }
        CoursePackage pkg = new CoursePackage();
        BeanUtils.copyProperties(dto, pkg);
        pkg.setId(id);
        int updated = courseMapper.updatePackage(pkg);
        if (updated != 1) {
            throw new BizException("课程套餐不存在");
        }
    }

    @Override
    public void deletePackage(Long id) {
        if (id == null || id < 0) {
            throw new BizException("套餐id不合法");
        }
        int updated = courseMapper.deletePackage(id);
        if (updated != 1) {
            throw new BizException("课程套餐不存在");
        }
    }

    @Override
    public void batchCreateSlots(Long packageId, BatchSlotDTO dto) {
        if (packageId == null || packageId < 0) {
            throw new BizException("套餐id不合法");
        }
        if (courseMapper.getPackageById(packageId) == null) {
            throw new BizException("课程套餐不存在");
        }
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();
        if (start.isAfter(end)) {
            throw new BizException("开始日期不能晚于结束日期");
        }
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            // 该日期已有时段则跳过，避免重复生成
            if (courseMapper.countSlotByDate(packageId, d) > 0) {
                continue;
            }
            CourseSlot slot = new CourseSlot();
            slot.setPackageId(packageId);
            slot.setCourseDate(d);
            slot.setTimeRange(dto.getTimeRange());
            slot.setMaxCount(dto.getMaxCount());
            slot.setCurrentCount(0);
            slot.setStatus("AVAILABLE");
            courseMapper.insertSlot(slot);
        }
    }

    @Override
    public List<CourseSlot> slotList(Long packageId, LocalDate date) {
        if (packageId == null || packageId < 0) {
            throw new BizException("套餐id不合法");
        }
        return courseMapper.listSlots(packageId, date);
    }

    @Override
    public void updateSlot(Long id, UpdateSlotDTO dto) {
        if (id == null || id < 0) {
            throw new BizException("时段id不合法");
        }
        int updated = courseMapper.updateSlot(id, dto);
        if (updated != 1) {
            throw new BizException("时段不存在");
        }
    }

    @Override
    public PageResult<CourseAppointmentVO> appointmentList(int pageNum, int pageSize, Long packageId, LocalDate date, String status) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<CourseAppointmentVO> list = courseMapper.listAppointments(packageId, date, status);
        PageInfo<CourseAppointmentVO> pageInfo = new PageInfo<>(list);
        PageResult<CourseAppointmentVO> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    @Transactional
    public void confirmAppointment(Long id) {
        if (id == null || id < 0) {
            throw new BizException("预约id不合法");
        }
        //状态守卫在 SQL 里：仅 PENDING 可确认，重复确认返回 0 行
        if (courseMapper.confirmAppointment(id) != 1) {
            throw new BizException("预约不存在或已被处理");
        }
    }

    @Override
    @Transactional
    public void rejectAppointment(Long id) {
        if (id == null || id < 0) {
            throw new BizException("预约id不合法");
        }
        CourseAppointmentVO vo = courseMapper.selectByAppointmentId(id);
        if (vo == null) {
            throw new BizException("预约不存在");
        }
        //状态守卫在 SQL 里：仅 PENDING 可拒绝，防重复处理
        if (courseMapper.rejectAppointment(id) != 1) {
            throw new BizException("预约不存在或已被处理");
        }
        //拒绝预约与用户取消同逻辑：退还积分 + 释放时段名额
        userMapper.updatePoints(vo.getUserId(), vo.getPrice());
        courseMapper.decreaseCurrentCount(id);
    }

    @Override
    public String uploadReport(Long appointmentId, MultipartFile file) {
        if (appointmentId == null || appointmentId < 0) {
            throw new BizException("预约id不合法");
        }
        if (file == null || file.isEmpty()) {
            throw new BizException("上传文件不能为空");
        }
        String original = file.getOriginalFilename();
        if (original == null || !original.toLowerCase().endsWith(".pdf")) {
            throw new BizException("仅允许上传PDF文件");
        }
        if (file.getSize() > MAX_REPORT_SIZE) {
            throw new BizException("文件大小不能超过20MB");
        }
        if (courseMapper.selectByAppointmentId(appointmentId) == null) {
            throw new BizException("预约不存在");
        }
        String yyyyMM = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        String fileName = UUID.randomUUID() + ".pdf";
        Path dir = Paths.get(REPORT_DIR, yyyyMM);
        try {
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(fileName));
        } catch (IOException e) {
            throw new BizException("报告文件保存失败");
        }
        String reportUrl = "/report/" + yyyyMM + "/" + fileName;
        int updated = courseMapper.updateReportUrl(appointmentId, reportUrl);
        if (updated != 1) {
            throw new BizException("预约不存在");
        }
        return reportUrl;
    }
}

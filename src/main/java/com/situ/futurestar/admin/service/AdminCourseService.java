package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.dto.BatchSlotDTO;
import com.situ.futurestar.core.dto.CoursePackageDTO;
import com.situ.futurestar.core.dto.UpdateSlotDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface AdminCourseService {

    PageResult<CoursePackage> packageList(int pageNum, int pageSize);

    void createPackage(CoursePackageDTO dto);

    void updatePackage(Long id, CoursePackageDTO dto);

    void deletePackage(Long id);

    void batchCreateSlots(Long packageId, BatchSlotDTO dto);

    List<CourseSlot> slotList(Long packageId, LocalDate date);

    void updateSlot(Long id, UpdateSlotDTO dto);

    PageResult<CourseAppointmentVO> appointmentList(int pageNum, int pageSize, Long packageId, LocalDate date, String status);

    //管理员确认预约
    void confirmAppointment(Long id);

    //管理员拒绝预约（退还积分、释放名额）
    void rejectAppointment(Long id);

    String uploadReport(Long appointmentId, MultipartFile file);
}

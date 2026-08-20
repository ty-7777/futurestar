package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.dto.UpdateSlotDTO;
import com.situ.futurestar.core.entity.CourseAppointment;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CourseMapper {
    // ---------- 会员端 ----------
    List<CoursePackage> list(@Param("keyword") String keyword);

    CoursePackage getPackageById(Long id);

    List<CourseSlot> getSlots(@Param("id") Long id, @Param("date") LocalDate date);
    CourseSlot selectBySlotId(Long id);
    List<CourseAppointmentVO> selectMyAppointment(@Param("userId") Long userId, @Param("status") String status);

    int plusCurrentCount(Long slotId);
    void createAppointment(@Param("userId") Long userId ,@Param("slotId") Long slotId, @Param("packageId") Long packageId);

    CourseAppointmentVO selectByAppointmentId(Long id);
    int updateAppointmentStatus(Long id);

    void decreaseCurrentCount(Long id);

    // ---------- 管理端 ----------
    List<CoursePackage> listAll();

    int insertPackage(CoursePackage coursePackage);

    int updatePackage(CoursePackage coursePackage);

    int deletePackage(Long id);

    List<CourseSlot> listSlots(@Param("packageId") Long packageId, @Param("date") LocalDate date);

    int updateSlot(@Param("id") Long id, @Param("dto") UpdateSlotDTO dto);

    List<CourseAppointmentVO> listAppointments(@Param("packageId") Long packageId,
                                               @Param("date") LocalDate date,
                                               @Param("status") String status);

    int insertSlot(CourseSlot slot);

    int countSlotByDate(@Param("packageId") Long packageId, @Param("date") LocalDate date);

    int updateReportUrl(@Param("id") Long id, @Param("reportUrl") String reportUrl);

    //管理员确认预约（仅 PENDING 可确认，防重复处理）
    int confirmAppointment(Long id);

    //管理员拒绝预约（仅 PENDING 可拒绝，防重复处理）
    int rejectAppointment(Long id);

    // ---------- 定时任务 ----------
    int completeFinishedAppointments(LocalDate today);

    int closeExpiredSlots(LocalDate today);

    int archiveOldAppointments(LocalDateTime before);

    List<CourseAppointmentVO> listAppointmentsByDate(LocalDate date);
}

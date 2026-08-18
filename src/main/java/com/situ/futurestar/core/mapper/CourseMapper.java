package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.CourseAppointment;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CourseMapper {
    List<CoursePackage> list();

    CoursePackage getPackageById(Long id);

    List<CourseSlot> getSlots(@Param("id") Long id, @Param("date") LocalDate date);
    CourseSlot selectBySlotId(Long id);
    List<CourseAppointmentVO> selectMyAppointment(@Param("userId") Long userId, @Param("status") String status);

    void plusCurrentCount(Long slotId);
    void createAppointment(@Param("userId") Long userId ,@Param("slotId") Long slotId, @Param("packageId") Long packageId);

    CourseAppointmentVO selectByAppointmentId(Long id);
    int updateAppointmentStatus(Long id);

    void decreaseCurrentCount(Long id);
}

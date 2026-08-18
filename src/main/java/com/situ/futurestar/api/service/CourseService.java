package com.situ.futurestar.api.service;


import com.situ.futurestar.core.dto.CreateAppointmentDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.CourseSlotVO;
import com.situ.futurestar.core.vo.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface CourseService {
    PageResult<CoursePackage> packagesList(int pageNum, int pageSize);

    CoursePackage getPackageById(Long id);

    List<CourseSlotVO> getSlots(Long id, LocalDate date);

    void submitAppointment(CreateAppointmentDTO appointmentDTO);


    PageResult<CourseAppointmentVO> myAppointmentList(int pageNum, int pageSize, String status);

    String report(Long id);

    void cancel(Long id);
}

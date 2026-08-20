package com.situ.futurestar.api.service;


import com.situ.futurestar.core.dto.CreateAppointmentDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.CourseSlotVO;
import com.situ.futurestar.core.vo.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface CourseService {
    PageResult<CoursePackage> packagesList(int pageNum, int pageSize, String keyword);

    CoursePackage getPackageById(Long id);

    List<CourseSlotVO> getSlots(Long id, LocalDate date);

    void submitAppointment(CreateAppointmentDTO appointmentDTO);

    //以下为 AI 客服工具调用专用重载（异步线程无 SecurityContext，显式传用户）
    void submitAppointment(CreateAppointmentDTO appointmentDTO, User user);

    PageResult<CourseAppointmentVO> myAppointmentList(int pageNum, int pageSize, String status);

    PageResult<CourseAppointmentVO> myAppointmentList(int pageNum, int pageSize, String status, User user);

    String report(Long id);

    void cancel(Long id);

    void cancel(Long id, User user);
}

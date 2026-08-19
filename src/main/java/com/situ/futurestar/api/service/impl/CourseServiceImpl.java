package com.situ.futurestar.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.CourseService;
import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.dto.CreateAppointmentDTO;
import com.situ.futurestar.core.entity.CourseAppointment;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.CourseSlot;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.CourseMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.CourseSlotVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    @Override
    public PageResult<CoursePackage> packagesList(int pageNum, int pageSize) {
        if(pageNum<0||pageSize<=0){
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum,pageSize);
        List<CoursePackage> list = courseMapper.list();
        PageInfo<CoursePackage> pageInfo =new PageInfo<>(list);
        PageResult<CoursePackage> result =new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public CoursePackage getPackageById(Long id) {
        if(id==null||id<0){
            throw  new BizException("id不合法");
        }
        CoursePackage coursePackage = courseMapper.getPackageById(id);
        if(coursePackage==null){
            throw  new BizException("课程套餐不存在");
        }
        return coursePackage;
    }

    @Override
    public List<CourseSlotVO> getSlots(Long id, LocalDate date) {
        if(id==null||id<0){
            throw  new BizException("id不合法");
        }
        List<CourseSlot> list = courseMapper.getSlots(id,date);
        if(list.isEmpty()){
            throw  new BizException("当天没有可预约的课程套餐");
        }
        List<CourseSlotVO> voList =new ArrayList<>();
        for (CourseSlot courseSlot : list) {
            CourseSlotVO vo =new CourseSlotVO();
            BeanUtils.copyProperties(courseSlot,vo);
            vo.setRemaining(courseSlot.getMaxCount() - courseSlot.getCurrentCount());
            voList.add(vo);
        }
        return voList;
    }

    @Override
    @Transactional
    public void submitAppointment(CreateAppointmentDTO appointmentDTO) {
        //校验用户状态正常
        User currentUser = SecurityUtil.getCurrentUser();
        if(!"ENABLED".equals(currentUser.getStatus())){
            throw  new BizException("当前用户已被禁用，无法预约课程");
        }
        Long slotId = appointmentDTO.getSlotId();
        //查询当前时间段并判断是否满足预约条件
        CourseSlot courseSlot = courseMapper.selectBySlotId(slotId);
        if(courseSlot==null || courseSlot.getDeleted()){
            throw  new BizException("当前时间段的课程不存在或已被删除");
        }
        if(courseSlot.getMaxCount().equals(courseSlot.getCurrentCount())
        ||!courseSlot.getStatus().equals("AVAILABLE")){
             throw new BizException(ErrorCode.CONFLICT,"当前时间段的课程预约人数已满");
        }
        //校验用户积分是否充足
        Integer points = currentUser.getPoints();
        if(points<=0){
            throw new BizException(ErrorCode.CONFLICT, "用户积分不足,无法预约");
        }
        Long packageId = courseSlot.getPackageId();
        CoursePackage coursePackage = courseMapper.getPackageById(packageId);
        if(coursePackage==null){
            throw  new BizException("课程套餐不存在");
        }
        Integer price = coursePackage.getPrice();
        if(points-price<0){
            throw new BizException(ErrorCode.CONFLICT, "用户积分不足,无法预约");
        }
        //走到这里说明具备预约资格
        Long userId = currentUser.getId();
        //原子扣减用户积分（SQL 带 points >= price 守卫，防止扣成负数）
        int deducted = userMapper.decreasePoints(userId,price);
        if(deducted!=1){
            throw new BizException(ErrorCode.CONFLICT, "用户积分不足,无法预约");
        }
        //原子增加时段预约人数（并发抢最后一个名额时更新0行说明已满，抛异常回滚扣掉的积分）
        if(courseMapper.plusCurrentCount(slotId)!=1){
            throw new BizException(ErrorCode.CONFLICT, "手慢了，名额已被抢完");
        }
        //创建预约记录
        courseMapper.createAppointment(userId,slotId,packageId);
        //TODO:预约成功，发送短信通知用户
    }

    @Override
    public PageResult<CourseAppointmentVO> myAppointmentList(int pageNum, int pageSize, String status) {
        if(pageNum<0||pageSize<=0){
            throw new BizException("分页参数不合法");
        }
        if(status.isBlank()){
            throw new BizException("状态名不合法");
        }
        Long userId = SecurityUtil.getCurrentUserId();
        PageHelper.startPage(pageNum,pageSize);
        List<CourseAppointmentVO> voList = courseMapper.selectMyAppointment(userId, status);
        PageInfo<CourseAppointmentVO> pageInfo =new PageInfo<>(voList);
        PageResult<CourseAppointmentVO>  result =new PageResult<>();
        //判断是否为空
        if(voList.isEmpty()){
            result.setList(List.of());
            result.setPageNum(pageNum);
            result.setPageSize(pageSize);
            result.setPages(0);
            result.setTotal(0);
            return  result;
        }
        //封装
        result.setList(voList);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages(pageInfo.getPages());
        result.setTotal(pageInfo.getTotal());
        return  result;
    }

    @Override
    public String report(Long id) {
        if(id==null||id<0){
            throw  new BizException("预约id不合法");
        }
        CourseAppointmentVO appointmentVO = courseMapper.selectByAppointmentId(id);
        if(appointmentVO==null){
            throw new BizException("该id的预约记录不存在");
        }
        String report = appointmentVO.getReportUrl();
        Long userId = appointmentVO.getUserId();
        if(!Objects.equals(SecurityUtil.getCurrentUserId(), userId)){
            throw  new BizException(ErrorCode.FORBIDDEN,"权限不足，你不能访问其他用户的报告");
        }
        if(report==null||report.isBlank()){
            throw new BizException("该预约还未生成报告");
        }
        return report;
    }

    @Override
    @Transactional
    public void cancel(Long id) {
        if(id==null||id<0){
            throw  new BizException("预约id不合法");
        }
        //检查预约状态是否可取消（未过期且未完成）
        CourseAppointmentVO courseAppointmentVO = courseMapper.selectByAppointmentId(id);
        if(courseAppointmentVO==null){
            throw new BizException("该id的预约记录不存在");
        }
        //先检查是否是该用户的预约
        Long userId = SecurityUtil.getCurrentUserId();
        if (!Objects.equals(courseAppointmentVO.getUserId(), userId)) {
            throw  new BizException(ErrorCode.FORBIDDEN,"禁止取消其他用户的预约");
        }
        //再检查当前预约的状态
        if(courseAppointmentVO.getStatus().equals("COMPLETED")){
            throw new BizException("该预约已完成，无法取消");
        }
        if("CANCELED".equals(courseAppointmentVO.getStatus())){//重复取消会重复退还积分和人数
            throw new BizException("该预约已取消，请勿重复取消");
        }

        //再检查时间，是不是在课程开始之后
        LocalDate localDate = LocalDate.now();//获取当前日期，不含时分秒
        LocalTime localTime = LocalTime.now();//获取当前时分秒
        String timeRange = courseAppointmentVO.getTimeRange();
        LocalDate courseDate = courseAppointmentVO.getCourseDate();
        String[] split = timeRange.split("-");
        LocalTime begin = LocalTime.parse(split[0]);
        LocalTime end = LocalTime.parse(split[1]);
        if(localDate.isAfter(courseDate)&&localTime.isAfter(end)){
            throw new BizException("课程已结束，无法取消预约");
        }
        if(localDate.isEqual(courseDate)&&localTime.isAfter(begin)&&localTime.isBefore(end)){
            throw new BizException("课程正在进行中，无法取消预约");
        }
        //走到这说明满足取消预约的条件
        //数据库原子退还用户积分
        Integer price = courseAppointmentVO.getPrice();
        userMapper.updatePoints(userId,price);
        //数据库原子减少时段预约人数
        courseMapper.decreaseCurrentCount(id);
        //更新预约状态为已取消
        int updated = courseMapper.updateAppointmentStatus(id);
        if(updated!=1){
            throw new BizException("预约记录已被删除，取消失败");
        }
        // TODO:发送取消通知短信

    }
}

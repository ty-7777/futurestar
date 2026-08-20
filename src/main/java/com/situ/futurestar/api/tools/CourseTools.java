package com.situ.futurestar.api.tools;

import com.situ.futurestar.api.service.CourseService;
import com.situ.futurestar.core.dto.CreateAppointmentDTO;
import com.situ.futurestar.core.entity.CoursePackage;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.CourseSlotVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * AI 智能客服 - 课程工具
 * <p>
 * 模型通过 Function Calling 调用这些方法，执行真实的课程查询/预约/取消。
 * 方法上的 @Tool/@ToolParam 描述会被框架转成 JSON Schema 发给模型，模型据此选择工具和填参。
 * <p>
 * 注意：工具在异步线程执行，SecurityContextHolder 拿不到登录用户，
 * 必须通过 ToolContext（由 ChatServiceImpl 注入）取当前用户，再传给 Service 重载方法。
 */
@Component
@RequiredArgsConstructor
public class CourseTools {

    /** ToolContext 中存放登录用户的 key（与 ChatServiceImpl 保持一致） */
    public static final String USER_KEY = "user";

    private final CourseService courseService;

    /** 从工具上下文取当前登录用户（工具执行线程无 SecurityContext，只能从这里拿） */
    private static User currentUser(ToolContext context) {
        User user = (User) context.getContext().get(USER_KEY);
        if (user == null) {
            throw new BizException("未获取到登录用户，请重新登录后再试");
        }
        return user;
    }

    /** 查询所有课程套餐（id/名称/价格积分/教练/适合水平） */
    @Tool(description = "查询所有课程套餐列表，返回套餐ID、名称、价格（积分）、教练、适合水平")
    public List<CoursePackage> listPackages() {
        return courseService.packagesList(1, 20, null).getList();
    }

    /** 查询某套餐某日期的可预约时段 */
    @Tool(description = "查询某课程套餐在某日期的可预约时段，返回时段ID、日期、时间段、剩余名额。date 参数格式必须为 yyyy-MM-dd（如 2026-08-21），用户没说清楚日期时先向用户确认再调用")
    public List<CourseSlotVO> listSlots(
            @ToolParam(description = "课程套餐ID") Long packageId,
            @ToolParam(description = "日期，格式 yyyy-MM-dd") String date) {
        LocalDate courseDate;
        try {
            courseDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new BizException("日期格式不正确，请提供 yyyy-MM-dd 格式的日期（如 2026-08-21）");
        }
        return courseService.getSlots(packageId, courseDate);
    }

    /** 提交课程预约（扣积分、防超卖等校验都在 Service 层） */
    @Tool(description = "为当前登录用户提交课程预约，成功后扣减积分。slotId 必须来自查询可预约时段的结果，预约前先向用户确认日期和时段")
    public String bookAppointment(
            @ToolParam(description = "课程时段ID") Long slotId,
            ToolContext context) {
        CreateAppointmentDTO dto = new CreateAppointmentDTO();
        dto.setSlotId(slotId);
        courseService.submitAppointment(dto, currentUser(context));
        return "预约成功";
    }

    /** 我的课程预约 */
    @Tool(description = "查询当前登录用户的课程预约列表，返回预约ID、课程名称、日期、时间段、状态。status 取值为：PENDING待确认/CONFIRMED已确认/CANCELED已取消/COMPLETED已完成，未指定时默认 PENDING")
    public List<CourseAppointmentVO> listMyAppointments(
            @ToolParam(description = "预约状态：PENDING/CONFIRMED/CANCELED/COMPLETED") String status,
            ToolContext context) {
        PageResult<CourseAppointmentVO> page = courseService.myAppointmentList(1, 20, status, currentUser(context));
        return page.getList();
    }

    /** 取消我的预约（退还积分） */
    @Tool(description = "取消当前登录用户的课程预约，成功后退还积分。appointmentId 来自查看我的预约的返回结果，取消前先向用户确认")
    public String cancelAppointment(
            @ToolParam(description = "预约ID") Long appointmentId,
            ToolContext context) {
        courseService.cancel(appointmentId, currentUser(context));
        return "取消成功，积分已退还";
    }
}

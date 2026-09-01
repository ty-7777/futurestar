package com.situ.futurestar.core.config;

import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.mapper.AiConversationMessageMapper;
import com.situ.futurestar.core.mapper.CourseMapper;
import com.situ.futurestar.core.mapper.EventMapper;
import com.situ.futurestar.core.mapper.MessageMapper;
import com.situ.futurestar.core.mapper.PhysicalRecordMapper;
import com.situ.futurestar.core.mapper.SmsCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统定时任务
 * 说明：单机项目用 Spring @Scheduled 即可；任务失败需记录告警日志便于人工介入。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final SmsCodeMapper smsCodeMapper;
    private final EventMapper eventMapper;
    private final CourseMapper courseMapper;
    private final MessageMapper messageMapper;
    private final AiConversationMessageMapper aiConversationMessageMapper;
    private final PhysicalRecordMapper physicalRecordMapper;

    /** 1. 清理过期短信验证码（每10分钟） */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void cleanupExpiredSmsCode() {
        try {
            int n = smsCodeMapper.deleteExpired(LocalDateTime.now());
            if (n > 0) {
                log.info("定时任务：清理过期短信验证码 {} 条", n);
            }
        } catch (Exception e) {
            log.error("定时任务[清理过期验证码]执行失败", e);
        }
    }

    /** 2. 赛事状态流转：DRAFT→REGISTRATING→IN_PROGRESS→ENDED（每分钟） */
    @Scheduled(cron = "0 * * * * ?")
    public void syncEventStatus() {
        try {
            LocalDateTime now = LocalDateTime.now();
            eventMapper.updateStatusToRegistering(now);
            eventMapper.updateStatusToInProgress(now);
            eventMapper.updateStatusToEnded(now);
        } catch (Exception e) {
            log.error("定时任务[赛事状态流转]执行失败", e);
        }
    }

    /** 3. 课程预约完成：课程日期已过且 CONFIRMED 置 COMPLETED（每分钟） */
    @Scheduled(cron = "0 * * * * ?")
    public void completeFinishedAppointments() {
        try {
            int n = courseMapper.completeFinishedAppointments(LocalDate.now());
            if (n > 0) {
                log.info("定时任务：完成 {} 条过期课程预约", n);
            }
        } catch (Exception e) {
            log.error("定时任务[课程预约完成]执行失败", e);
        }
    }

    /** 4. 课程时段关闭：课程日期已过且未关闭的置 CLOSED（每天1点） */
    @Scheduled(cron = "0 0 1 * * ?")
    public void closeExpiredSlots() {
        try {
            int n = courseMapper.closeExpiredSlots(LocalDate.now());
            if (n > 0) {
                log.info("定时任务：关闭 {} 个过期课程时段", n);
            }
        } catch (Exception e) {
            log.error("定时任务[课程时段关闭]执行失败", e);
        }
    }

    /** 5. 预约提醒：次日有课的推送站内消息（每天9点） */
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendAppointmentReminder() {
        try {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            List<CourseAppointmentVO> list = courseMapper.listAppointmentsByDate(tomorrow);
            for (CourseAppointmentVO a : list) {
                Message m = new Message();
                m.setUserId(a.getUserId());
                m.setTitle("课程预约提醒");
                m.setContent("您明天（" + tomorrow + "）有课程：【" + a.getPackageName() + "】 " + a.getTimeRange() + "，请准时参加。");
                m.setType("COURSE");
                m.setIsRead(false);
                messageMapper.insert(m);
            }
            log.info("定时任务：推送明日课程提醒 {} 条", list.size());
        } catch (Exception e) {
            log.error("定时任务[预约提醒推送]执行失败", e);
        }
    }

    /** 6. 清理过期 AI 对话消息：按 6 个月保留策略（每天3点） */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredAiMessages() {
        try {
            LocalDateTime before = LocalDateTime.now().minusMonths(6);
            int n = aiConversationMessageMapper.deleteExpired(before);
            if (n > 0) {
                log.info("定时任务：清理过期AI对话消息 {} 条", n);
            }
        } catch (Exception e) {
            log.error("定时任务[清理AI消息]执行失败", e);
        }
    }

    /** 7. 历史预约归档：2 年前预约软删除（每天4点） */
    @Scheduled(cron = "0 0 4 * * ?")
    public void archiveOldAppointments() {
        try {
            LocalDateTime before = LocalDateTime.now().minusYears(2);
            int n = courseMapper.archiveOldAppointments(before);
            if (n > 0) {
                log.info("定时任务：归档 {} 条历史预约", n);
            }
        } catch (Exception e) {
            log.error("定时任务[历史预约归档]执行失败", e);
        }
    }

}

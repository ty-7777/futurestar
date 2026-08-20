package com.situ.futurestar.api.tools;

import com.situ.futurestar.api.service.EventService;
import com.situ.futurestar.core.entity.MatchEvent;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * AI 智能客服 - 赛事工具
 * <p>
 * 薄壳设计：只负责参数接收与校验，业务逻辑全部复用 EventService。
 * 返回对象列表由框架自动 JSON 序列化回填给模型；参数不对直接抛 BizException，模型会转述给用户。
 */
@Component
@RequiredArgsConstructor
public class EventTools {

    private final EventService eventService;

    /** 从工具上下文取当前登录用户（复制 CourseTools 的写法即可） */
    private static User currentUser(ToolContext context) {
        User user = (User) context.getContext().get(CourseTools.USER_KEY);
        if (user == null) {
            throw new BizException("未获取到登录用户，请重新登录后再试");
        }
        return user;
    }

    /** 查询赛事列表（type 必填：MATCH比赛/CAMP训练营/SELECTION选拔） */
    @Tool(description = "查询赛事活动列表，返回赛事ID、标题、类型、报名时间、人数上限与当前人数。type 取值为：MATCH比赛/CAMP训练营/SELECTION选拔，用户没说类型时先向用户确认")
    public List<MatchEvent> listEvents(
            @ToolParam(description = "赛事类型：MATCH/CAMP/SELECTION") String type) {
        return eventService.eventList(1, 20, type, null).getList();
    }

    /** 报名赛事（防重复靠唯一索引，报错会抛 BizException） */
    @Tool(description = "为当前登录用户报名赛事。eventId 必须来自查询赛事列表的结果，报名前先向用户确认")
    public String registerEvent(
            @ToolParam(description = "赛事ID") Long eventId,
            ToolContext context) {
        eventService.signUp(eventId, currentUser(context));
        return "报名成功";
    }

    /** 查看我报名的赛事 */
    @Tool(description = "查询当前登录用户已报名的赛事列表")
    public List<MatchEvent> listMyEvents(ToolContext context) {
        return eventService.myEvent(1, 20, currentUser(context)).getList();
    }
}

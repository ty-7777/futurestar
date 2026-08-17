package com.situ.futurestar.core.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息实体类
 * 对应数据库表：message（消息表）
 */
@Data
public class Message {

    /** 消息ID（主键，自增） */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 消息标题 */
    private String title;

    /** 消息内容 */
    private String content;

    /** 类型：COURSE 课程 / EVENT 赛事 / PHYSICAL 体能 / SYSTEM 系统 */
    private String type;

    /** 是否已读：0 未读 / 1 已读 */
    private Boolean isRead;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0 未删除 / 1 已删除 */
    private Boolean deleted;
}

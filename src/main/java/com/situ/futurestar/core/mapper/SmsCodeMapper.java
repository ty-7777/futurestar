package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;

@Mapper
public interface SmsCodeMapper {
    // 查该手机号最近一条未使用、未过期的验证码（按创建时间倒序取最新）
    SmsCode selectLatestValid(String phone);
    // 标记已使用
    int markUsed(Long id);
    int insert(SmsCode smsCode);

    // 物理清理过期验证码（定时任务）
    int deleteExpired(LocalDateTime now);
}

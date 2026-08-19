package com.situ.futurestar.api.service.impl;

import com.situ.futurestar.api.service.PromptService;
import com.situ.futurestar.core.entity.SysConfig;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PromptServiceImpl implements PromptService {
    private final ConfigMapper configMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String PREFIX = "ai:prompt:";

    @Override
    public String get(String key) {
        String cached = stringRedisTemplate.opsForValue().get(PREFIX + key);
        if (cached != null) return cached;                       // ① 先查缓存
        SysConfig config = configMapper.getByKey(key);            // ② 缓存没有才查库
        if (config == null) throw new BizException("该key在系统配置中不存在");
        stringRedisTemplate.opsForValue().set(PREFIX + key, config.getConfigValue());  // ③ 回写
        return config.getConfigValue();
    }
}

package com.situ.futurestar.admin.service.impl;

import com.situ.futurestar.admin.service.AdminConfigService;
import com.situ.futurestar.core.entity.SysConfig;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.ConfigMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminConfigServiceImpl implements AdminConfigService {
    private final ConfigMapper configMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public List<SysConfig> listAll() {
        return configMapper.listAll();
    }

    @Override
    public SysConfig getByKey(String key) {
        if (key == null || key.isBlank()) {
            throw new BizException("配置键不能为空");
        }
        SysConfig config = configMapper.getByKey(key);
        if (config == null) {
            throw new BizException("配置不存在");
        }
        return config;
    }

    @Override
    public void updateConfig(String key, String configValue) {
        if (key == null || key.isBlank()) {
            throw new BizException("配置键不能为空");
        }
        int updated = configMapper.updateValue(key, configValue);
        if (updated != 1) {
            throw new BizException("配置不存在");
        }
        stringRedisTemplate.delete("ai:prompt:" + key);
    }
}

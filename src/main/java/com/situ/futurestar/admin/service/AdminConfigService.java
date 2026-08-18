package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.entity.SysConfig;

import java.util.List;

public interface AdminConfigService {

    List<SysConfig> listAll();

    SysConfig getByKey(String key);

    void updateConfig(String key, String configValue);
}

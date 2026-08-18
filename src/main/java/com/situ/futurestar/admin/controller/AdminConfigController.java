package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.AdminConfigService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.UpdateConfigDTO;
import com.situ.futurestar.core.entity.SysConfig;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/config")
public class AdminConfigController {
    private final AdminConfigService adminConfigService;

    //配置列表
    @GetMapping
    public Result<List<SysConfig>> listAll() {
        return Result.success(adminConfigService.listAll());
    }

    //获取配置
    @GetMapping("/{key}")
    public Result<SysConfig> getByKey(@PathVariable("key") String key) {
        return Result.success(adminConfigService.getByKey(key));
    }

    //更新配置
    @PutMapping("/{key}")
    public Result<Void> updateConfig(@PathVariable("key") String key,
                                     @Valid @RequestBody UpdateConfigDTO dto) {
        adminConfigService.updateConfig(key, dto.getConfigValue());
        return Result.success();
    }
}

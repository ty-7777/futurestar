package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.PlayerService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.PlayerLevelDTO;
import com.situ.futurestar.core.dto.PlayerPointsDTO;
import com.situ.futurestar.core.dto.PlayerStatusDTO;
import com.situ.futurestar.core.vo.AdminPlayerDetailVO;
import com.situ.futurestar.core.vo.AdminPlayerVO;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/players")
public class PlayerController {
    private final PlayerService playerService;

    //球员列表
    @GetMapping
    public Result<PageResult<AdminPlayerVO>> playerList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String status
    ) {
        return Result.success(playerService.playerList(pageNum, pageSize, keyword, position, status));
    }

    //球员详情
    @GetMapping("/{id}")
    public Result<AdminPlayerDetailVO> playerDetail(@PathVariable("id") Long id) {
        return Result.success(playerService.playerDetail(id));
    }

    //启用/禁用
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody PlayerStatusDTO dto) {
        playerService.updateStatus(id, dto.getStatus());
        return Result.success();
    }

    //调整等级
    @PutMapping("/{id}/level")
    public Result<Void> updateLevel(@PathVariable("id") Long id, @Valid @RequestBody PlayerLevelDTO dto) {
        playerService.updateLevel(id, dto.getMemberLevel());
        return Result.success();
    }

    //调整积分
    @PutMapping("/{id}/points")
    public Result<Void> updatePoints(@PathVariable("id") Long id, @Valid @RequestBody PlayerPointsDTO dto) {
        playerService.updatePoints(id, dto.getDelta(), dto.getReason());
        return Result.success();
    }

    //重置密码
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable("id") Long id) {
        playerService.resetPassword(id);
        return Result.success();
    }
}

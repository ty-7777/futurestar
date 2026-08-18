package com.situ.futurestar.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.admin.service.PlayerService;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.PhysicalRecordMapper;
import com.situ.futurestar.core.mapper.PlayerMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.vo.AdminPlayerDetailVO;
import com.situ.futurestar.core.vo.AdminPlayerVO;
import com.situ.futurestar.core.vo.CourseAppointmentVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
    /** 重置密码的固定默认值（无短信通知，重置后需用户自行修改） */
    private static final String DEFAULT_RESET_PASSWORD = "123456";

    private final PlayerMapper playerMapper;
    private final PhysicalRecordMapper physicalRecordMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResult<AdminPlayerVO> playerList(int pageNum, int pageSize, String keyword, String position, String status) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = playerMapper.selectPlayerList(keyword, position, status);
        PageInfo<User> pageInfo = new PageInfo<>(list);
        List<AdminPlayerVO> voList = new ArrayList<>();
        for (User user : list) {
            AdminPlayerVO vo = new AdminPlayerVO();
            BeanUtils.copyProperties(user, vo);
            voList.add(vo);
        }
        PageResult<AdminPlayerVO> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(voList);
        return result;
    }

    @Override
    public AdminPlayerDetailVO playerDetail(Long id) {
        if (id == null || id < 0) {
            throw new BizException("id不合法");
        }
        User player = playerMapper.selectPlayerById(id);
        if (player == null) {
            throw new BizException("球员不存在");
        }
        AdminPlayerDetailVO vo = new AdminPlayerDetailVO();
        BeanUtils.copyProperties(player, vo);
        List<PhysicalRecord> physicalRecords = physicalRecordMapper.selectListByUserId(id);
        List<CourseAppointmentVO> appointments = playerMapper.selectAppointmentsByUserId(id);
        vo.setPhysicalRecords(physicalRecords);
        vo.setAppointments(appointments);
        return vo;
    }

    @Override
    public void updateStatus(Long id, String status) {
        if (id == null || id < 0) {
            throw new BizException("id不合法");
        }
        if (status == null || status.isBlank()) {
            throw new BizException("状态不能为空");
        }
        int updated = playerMapper.updateStatus(id, status);
        if (updated != 1) {
            throw new BizException("球员不存在");
        }
    }

    @Override
    public void updateLevel(Long id, String memberLevel) {
        if (id == null || id < 0) {
            throw new BizException("id不合法");
        }
        if (memberLevel == null || memberLevel.isBlank()) {
            throw new BizException("会员等级不能为空");
        }
        int updated = playerMapper.updateLevel(id, memberLevel);
        if (updated != 1) {
            throw new BizException("球员不存在");
        }
    }

    @Override
    public void updatePoints(Long id, Integer delta, String reason) {
        if (id == null || id < 0) {
            throw new BizException("id不合法");
        }
        if (delta == null) {
            throw new BizException("积分变更量不能为空");
        }
        if (playerMapper.selectPlayerById(id) == null) {
            throw new BizException("球员不存在");
        }
        // delta 可正可负（正加负减），复用 user 表的 points + delta
        userMapper.updatePoints(id, delta);
        // TODO: reason 变更原因当前无积分流水表可存，后续如需审计可新增积分流水表
    }

    @Override
    public void resetPassword(Long id) {
        if (id == null || id < 0) {
            throw new BizException("id不合法");
        }
        if (playerMapper.selectPlayerById(id) == null) {
            throw new BizException("球员不存在");
        }
        userMapper.updatePassword(id, passwordEncoder.encode(DEFAULT_RESET_PASSWORD));
    }
}

package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.vo.AdminPlayerDetailVO;
import com.situ.futurestar.core.vo.AdminPlayerVO;
import com.situ.futurestar.core.vo.PageResult;

public interface PlayerService {

    PageResult<AdminPlayerVO> playerList(int pageNum, int pageSize, String keyword, String position, String status);

    AdminPlayerDetailVO playerDetail(Long id);

    void updateStatus(Long id, String status);

    void updateLevel(Long id, String memberLevel);

    void updatePoints(Long id, Integer delta, String reason);

    void resetPassword(Long id);
}

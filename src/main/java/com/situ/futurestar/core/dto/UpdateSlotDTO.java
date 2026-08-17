package com.situ.futurestar.core.dto;

import lombok.Data;

/**
 * 课程时段修改请求 DTO（管理端，修改最大人数/状态）
 */
@Data
public class UpdateSlotDTO {

    /** 最大预约人数 */
    private Integer maxCount;

    /** 状态：AVAILABLE / FULL / CLOSED */
    private String status;
}

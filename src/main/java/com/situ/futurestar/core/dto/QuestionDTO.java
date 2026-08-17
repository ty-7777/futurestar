package com.situ.futurestar.core.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 题目新增/修改请求 DTO（管理端）
 */
@Data
public class QuestionDTO {

    /** 题目内容 */
    @NotBlank(message = "题目内容不能为空")
    private String content;

    /** 类型：SINGLE / MULTIPLE / TEXT */
    @NotBlank(message = "题目类型不能为空")
    private String type;

    /** 选项JSON数组（SINGLE/MULTIPLE 必填） */
    private String options;

    /** 排序号 */
    private Integer sortOrder;
}

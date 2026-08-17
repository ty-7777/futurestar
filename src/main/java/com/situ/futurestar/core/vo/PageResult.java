package com.situ.futurestar.core.vo;


import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    private long total;      // 总条数
    private List<T> list;    // 当前页数据
    private int pageNum;     // 当前页码
    private int pageSize;    // 每页大小
    private int pages;       // 总页数
}

package com.situ.futurestar.core.vo;

import lombok.Data;

import java.util.List;

/**
 * 单个指标的统计分析 VO（平均值/最大值/最小值/时间点数据）
 */
@Data
public class TrendMetricVO {

    /** 平均值 */
    private Double avg;

    /** 最大值 */
    private Double max;

    /** 最小值 */
    private Double min;

    /** 按时间排序的数据点 */
    private List<Double> points;
}

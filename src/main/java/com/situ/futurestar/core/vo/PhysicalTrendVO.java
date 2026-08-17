package com.situ.futurestar.core.vo;

import lombok.Data;

import java.util.List;

/**
 * 体能趋势分析 VO
 */
@Data
public class PhysicalTrendVO {

    /** 体重趋势 */
    private TrendMetricVO weight;

    /** BMI趋势 */
    private TrendMetricVO bmi;

    /** 30米冲刺趋势 */
    private TrendMetricVO sprint30m;
}

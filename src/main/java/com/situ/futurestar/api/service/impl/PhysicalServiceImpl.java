package com.situ.futurestar.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.PhysicalService;
import com.situ.futurestar.core.dto.PhysicalRecordDTO;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.PhysicalRecordMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.PhysicalTrendVO;
import com.situ.futurestar.core.vo.TrendMetricVO;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhysicalServiceImpl implements PhysicalService {
    private final PhysicalRecordMapper physicalRecordMapper;
    @Override
    public void recordPhysical(PhysicalRecordDTO physicalRecordDTO) {
        PhysicalRecord record=new PhysicalRecord();
        BeanUtils.copyProperties(physicalRecordDTO,record);
        if (record.getHeight() != null && record.getWeight() != null) {//这里注意要判空
            double heightM = record.getHeight() / 100;
            record.setBmi(record.getWeight() / (heightM * heightM));
        }//设置bmi
        Long userId = SecurityUtil.getCurrentUserId();
        record.setUserId(userId);//设置用户Id
        record.setRecordedAt(physicalRecordDTO.getRecordedAt() != null//设置体测时间，如果前端没有穿的话
                ? physicalRecordDTO.getRecordedAt() : LocalDateTime.now());
        physicalRecordMapper.insert(record);
        // TODO:触发训练/饮食指导生成（可选）

    }

    @Override
    public PageResult<PhysicalRecord> listPhysicalRecord(int pageNum, int pageSize) {
        if(pageNum<0||pageSize<=0){
            throw  new BizException("分页参数不合法");
        }
        //拿到当前用户
        Long userId = SecurityUtil.getCurrentUserId();
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        List<PhysicalRecord> list = physicalRecordMapper.selectListByUserId(userId);
        PageInfo<PhysicalRecord> pageInfo =new PageInfo<>(list);
        PageResult<PhysicalRecord> result = new PageResult<>();
        result.setList(list);
        result.setPages(pageInfo.getPages());
        result.setTotal(pageInfo.getTotal());
        result.setPageNum(pageInfo.getPageNum());
        result.setPageSize(pageInfo.getPageSize());
        return result;
    }

    @Override
    public PhysicalTrendVO trend(int months) {
        if(months<=0){
            throw  new BizException("月份不合法");
        }
        //拿到当前的用户id
        Long userId = SecurityUtil.getCurrentUserId();
        //查询该用户的近months个月的所有体能记录
        List<PhysicalRecord> trend = physicalRecordMapper.trend(userId, months);
        //处理体重趋势  算平均值，最大值，最小值，list
        List<Double>  weightList =new ArrayList<>();
        List<Double>  bmiList =new ArrayList<>();
        List<Double>  sprint30mList =new ArrayList<>();
        for (PhysicalRecord record : trend) {
            if (record.getWeight()!=null) {
                weightList.add(record.getWeight());
            }
            if (record.getBmi()!=null) {
                bmiList.add(record.getBmi());
            }
            if (record.getSprint30m()!=null) {
                sprint30mList.add(record.getSprint30m());
            }
        }
        //获取weight的vo
        TrendMetricVO weightVo = toTrendMetricVo(weightList);
        //获取bmi的Vo
        TrendMetricVO bmiVo = toTrendMetricVo(bmiList);
        //获取30米跑的Vo
        TrendMetricVO sprintVo = toTrendMetricVo(sprint30mList);
        PhysicalTrendVO physicalTrendVO =new PhysicalTrendVO();
        physicalTrendVO.setWeight(weightVo);
        physicalTrendVO.setBmi(bmiVo);
        physicalTrendVO.setSprint30m(sprintVo);
        return physicalTrendVO;
    }
    public TrendMetricVO toTrendMetricVo(List<Double> list){
        DoubleSummaryStatistics statistics = list.stream().mapToDouble(Double::doubleValue).summaryStatistics();
        if (list.isEmpty()) {
            TrendMetricVO vo = new TrendMetricVO();
            vo.setAvg(0.0); vo.setMax(0.0); vo.setMin(0.0);
            vo.setPoints(list);
            return vo;
        }
        TrendMetricVO vo =new TrendMetricVO();
       vo.setAvg(statistics.getAverage());
       vo.setMax(statistics.getMax());
       vo.setMin(statistics.getMin());
       vo.setPoints(list);
        return  vo;

    }
}

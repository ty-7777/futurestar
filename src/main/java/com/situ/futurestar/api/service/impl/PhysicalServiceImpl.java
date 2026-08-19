package com.situ.futurestar.api.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.PhysicalService;
import com.situ.futurestar.api.service.PromptService;
import com.situ.futurestar.core.dto.PhysicalRecordDTO;
import com.situ.futurestar.core.entity.Message;
import com.situ.futurestar.core.entity.PhysicalRecord;
import com.situ.futurestar.core.entity.TrainingGuidance;
import com.situ.futurestar.core.entity.User;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.MessageMapper;
import com.situ.futurestar.core.mapper.PhysicalRecordMapper;
import com.situ.futurestar.core.mapper.TrainingGuidanceMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.PageResult;
import com.situ.futurestar.core.vo.PhysicalTrendVO;
import com.situ.futurestar.core.vo.TrendMetricVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
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
    private final PromptService promptService;
    private final ChatClient chatClient;
    private final MessageMapper messageMapper;
    private final TrainingGuidanceMapper trainingGuidanceMapper;
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
        // 触发训练/饮食指导AI生成
        User currentUser = SecurityUtil.getCurrentUser();
        List<String> abnormal = checkAbnormal(record, currentUser);
        if(!abnormal.isEmpty()){
            //有不正常的指标，逐个生成个性化建议，通过站内消息推送给用户
            for (String indicator : abnormal) {
                generateGuidance(userId, guidanceType(indicator), indicator);
            }
        }

    }

    //检查球员记录是否触发判定
    private List<String> checkAbnormal(PhysicalRecord r, User user){
        List<String> list = new ArrayList<>();
        boolean male = "男".equals(user.getGender());//男女的评判指标不同
        if (r.getBmi() != null && (r.getBmi() < 18.5 || r.getBmi() >= 24))//检查BMI正不正常
            list.add("BMI=" + r.getBmi() + "(正常18.5-24)");//不正常就放入数组
        if (r.getBodyFatRate() != null) {//检查体脂率正不正常
            double lo = male ? 12 : 18, hi = male ? 18 : 24;
            if (r.getBodyFatRate() < lo || r.getBodyFatRate() > hi)
                list.add("体脂率=" + r.getBodyFatRate() + "%(正常" + lo + "-" + hi + ")");
        }
        if (r.getHeartRate() != null && (r.getHeartRate() < 60 || r.getHeartRate() > 100))//检查心率正不正常
            list.add("静息心率=" + r.getHeartRate() + "(正常60-100)");
        if (r.getVitalCapacity() != null && r.getVitalCapacity() < (male ? 3500 : 2500))//检查肺活量正不正常
            list.add("肺活量=" + r.getVitalCapacity() + "ml(正常≥" + (male ? 3500 : 2500) + ")");
        return list;
    }
    //调用AI生成个性化建议
    private void generateGuidance(Long userId, String type, String indicator) {
        // 同一类型当天已生成过则跳过（去重）
        if (trainingGuidanceMapper.existToday(userId, type) > 0) {
            return;
        }
        String prompt = promptService.get("ai_guidance_" + type.toLowerCase() + "_prompt");
        User user = SecurityUtil.getCurrentUser();
        // 拿文本
        String context = "身高" + user.getHeight() + "cm，体重" + user.getWeight() + "kg，性别" + user.getGender()
                + "。超标指标：" + indicator;
        String suggestion = chatClient.prompt()
                .system(prompt)
                .user(context)
                .call()
                .content();
        // 落库
        TrainingGuidance g = new TrainingGuidance();
        g.setUserId(userId);
        g.setType(type);
        g.setContent(suggestion);
        g.setIsRead(false);
        trainingGuidanceMapper.insert(g);

        Message m = new Message();                            // 站内信推送
        m.setUserId(userId); m.setTitle("体能异常提醒");
        m.setContent(suggestion);   // 建议全文作为消息内容
        m.setType("PHYSICAL");
        m.setIsRead(false);
        messageMapper.insert(m);
    }

    // 根据超标指标决定建议类型：体重/体脂→饮食，心肺→训练
    private String guidanceType(String indicator) {
        if (indicator.startsWith("BMI") || indicator.startsWith("体脂率")) return "DIET";
        return "TRAINING";
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

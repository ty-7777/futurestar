package com.situ.futurestar.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.api.service.AssessmentService;
import com.situ.futurestar.core.common.ErrorCode;
import com.situ.futurestar.core.dto.SubmitAssessmentDTO;
import com.situ.futurestar.core.entity.AssessmentResult;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.AssessmentMapper;
import com.situ.futurestar.core.mapper.UserMapper;
import com.situ.futurestar.core.util.SecurityUtil;
import com.situ.futurestar.core.vo.AssessmentResultVO;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    private  final  AssessmentMapper assessmentMapper;
    private final UserMapper userMapper;
    @Override
    public List<Questionnaire> getList() {
        List<Questionnaire> list = assessmentMapper.getList();
        return list;
    }

    @Override
    public List<Question> getQuestions(Long id) {
        List<Question> questionList = assessmentMapper.getQuestions(id);
        return questionList;
    }

    @Override
    @Transactional
    public AssessmentResultVO submit(SubmitAssessmentDTO submitAssessmentDTO) {
        //dto只校验了id不能为空，这里还要先校验答案json不能为空
        String answers = submitAssessmentDTO.getAnswers();
        if(answers==null||answers.isBlank()){
            throw new BizException("答案不能为空");
        }
        //获取用户id
        Long userId = SecurityUtil.getCurrentUserId();
        //TODO：调用ai对评测结果生成评分和建议
        AssessmentResult result =new AssessmentResult();
        BeanUtils.copyProperties(submitAssessmentDTO,result);
        result.setUserId(userId);
        //把评测结果存入数据库
        assessmentMapper.saveAssessment(result);
        //每次评测要奖励用户积分20
        userMapper.updatePoints(userId,20);
        AssessmentResultVO vo =new AssessmentResultVO();
        BeanUtils.copyProperties(result,vo);
        vo.setId(result.getId());
        vo.setCreateTime(LocalDateTime.now());
        return vo;
    }

    @Override
    public PageResult<AssessmentResultVO> getHistory(int pageNum, int pageSize) {
        if(pageNum<0||pageSize<=0){
            throw  new BizException("分页参数不合法");
        }
        PageResult<AssessmentResultVO> result =new PageResult<>();
       //获取当前用户的id
        Long userId = SecurityUtil.getCurrentUserId();
        PageHelper.startPage(pageNum,pageSize);
        List<AssessmentResult> list = assessmentMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo<>(list);
        if(list.isEmpty()){//如果用户没有评测过，需要返回空数据
            result.setPageSize(pageSize);
            result.setPageNum(pageNum);
            result.setTotal(0);
            result.setPages(0);
            result.setList(List.of());
            return  result;
        }
        //转vo
        List<AssessmentResultVO>  voList =new ArrayList<>();
        for (AssessmentResult assessmentResult : list) {
            AssessmentResultVO vo=new AssessmentResultVO();
            BeanUtils.copyProperties(assessmentResult,vo);
            voList.add(vo);
        }
       //封装
        result.setPageSize(pageInfo.getPageSize());
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setPageNum(pageNum);
        result.setList(voList);
        return result;
    }

    @Override
    public AssessmentResultVO getDetail(Long id) {
        if(id==null||id<0){
            throw  new BizException("id不合法");
        }
        AssessmentResult assessmentResult = assessmentMapper.selectById(id);
        if(assessmentResult==null){
            throw  new BizException("评测记录不存在");
        }
        Long userId = SecurityUtil.getCurrentUserId();//防止其他用户读该用户的评测记录
        if (!assessmentResult.getUserId().equals(userId)) throw new BizException(ErrorCode.FORBIDDEN, "无权访问");
        AssessmentResultVO vo =new AssessmentResultVO();
        BeanUtils.copyProperties(assessmentResult,vo);
        return vo;
    }
}

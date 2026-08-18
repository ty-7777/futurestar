package com.situ.futurestar.api.controller;


import com.situ.futurestar.api.service.AssessmentService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.SubmitAssessmentDTO;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.vo.AssessmentResultVO;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member/assessment")
public class AssessmentController {
    private final AssessmentService assessmentService;

    //获取问卷列表
    @GetMapping("/questionnaires")
    public Result<List<Questionnaire>>  getList(){
        return Result.success(assessmentService.getList());
    }
    //获取问卷题目
    @GetMapping("/questionnaires/{id}/questions")
    public Result<List<Question>> getQuestions(@PathVariable("id") Long id){
        return Result.success(assessmentService.getQuestions(id));
    }
    //用户答完题后提交评测结果
    @PostMapping()
    public Result<AssessmentResultVO> submit(@Valid@RequestBody SubmitAssessmentDTO submitAssessmentDTO){
        return Result.success(assessmentService.submit(submitAssessmentDTO));
    }
    //查询评测历史
    @GetMapping("/history")
    public Result<PageResult<AssessmentResultVO>> getHistory(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10")int pageSize
    ){
        return Result.success(assessmentService.getHistory(pageNum,pageSize));
    }
    //根据评测id查询评测详情
    @GetMapping("/{id}")
    public  Result<AssessmentResultVO> getDetail(@PathVariable("id")Long id){
        return Result.success(assessmentService.getDetail(id));
    }



}

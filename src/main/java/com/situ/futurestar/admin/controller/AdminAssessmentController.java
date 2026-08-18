package com.situ.futurestar.admin.controller;

import com.situ.futurestar.admin.service.AdminAssessmentService;
import com.situ.futurestar.core.common.Result;
import com.situ.futurestar.core.dto.QuestionDTO;
import com.situ.futurestar.core.dto.QuestionnaireDTO;
import com.situ.futurestar.core.dto.QuestionnaireStatusDTO;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.vo.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/assessment")
public class AdminAssessmentController {
    private final AdminAssessmentService adminAssessmentService;

    //问卷列表
    @GetMapping("/questionnaires")
    public Result<PageResult<Questionnaire>> questionnaireList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return Result.success(adminAssessmentService.questionnaireList(pageNum, pageSize));
    }

    //新增问卷
    @PostMapping("/questionnaires")
    public Result<Void> createQuestionnaire(@Valid @RequestBody QuestionnaireDTO dto) {
        adminAssessmentService.createQuestionnaire(dto);
        return Result.success();
    }

    //修改问卷
    @PutMapping("/questionnaires/{id}")
    public Result<Void> updateQuestionnaire(@PathVariable("id") Long id, @RequestBody QuestionnaireDTO dto) {
        adminAssessmentService.updateQuestionnaire(id, dto);
        return Result.success();
    }

    //删除问卷（逻辑删除）
    @DeleteMapping("/questionnaires/{id}")
    public Result<Void> deleteQuestionnaire(@PathVariable("id") Long id) {
        adminAssessmentService.deleteQuestionnaire(id);
        return Result.success();
    }

    //发布/下架
    @PutMapping("/questionnaires/{id}/status")
    public Result<Void> updateQuestionnaireStatus(@PathVariable("id") Long id,
                                                  @Valid @RequestBody QuestionnaireStatusDTO dto) {
        adminAssessmentService.updateQuestionnaireStatus(id, dto.getStatus());
        return Result.success();
    }

    //问卷题目列表
    @GetMapping("/questionnaires/{id}/questions")
    public Result<List<Question>> questionList(@PathVariable("id") Long questionnaireId) {
        return Result.success(adminAssessmentService.questionList(questionnaireId));
    }

    //新增题目
    @PostMapping("/questionnaires/{id}/questions")
    public Result<Void> createQuestion(@PathVariable("id") Long questionnaireId,
                                       @Valid @RequestBody QuestionDTO dto) {
        adminAssessmentService.createQuestion(questionnaireId, dto);
        return Result.success();
    }

    //修改题目
    @PutMapping("/questions/{id}")
    public Result<Void> updateQuestion(@PathVariable("id") Long id, @RequestBody QuestionDTO dto) {
        adminAssessmentService.updateQuestion(id, dto);
        return Result.success();
    }

    //删除题目（逻辑删除）
    @DeleteMapping("/questions/{id}")
    public Result<Void> deleteQuestion(@PathVariable("id") Long id) {
        adminAssessmentService.deleteQuestion(id);
        return Result.success();
    }
}

package com.situ.futurestar.admin.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.situ.futurestar.admin.service.AdminAssessmentService;
import com.situ.futurestar.core.dto.QuestionDTO;
import com.situ.futurestar.core.dto.QuestionnaireDTO;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.exception.BizException;
import com.situ.futurestar.core.mapper.AssessmentMapper;
import com.situ.futurestar.core.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAssessmentServiceImpl implements AdminAssessmentService {
    private final AssessmentMapper assessmentMapper;

    @Override
    public PageResult<Questionnaire> questionnaireList(int pageNum, int pageSize) {
        if (pageNum < 0 || pageSize <= 0) {
            throw new BizException("分页参数不合法");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Questionnaire> list = assessmentMapper.listAllQuestionnaires();
        PageInfo<Questionnaire> pageInfo = new PageInfo<>(list);
        PageResult<Questionnaire> result = new PageResult<>();
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setTotal(pageInfo.getTotal());
        result.setPages(pageInfo.getPages());
        result.setList(list);
        return result;
    }

    @Override
    public void createQuestionnaire(QuestionnaireDTO dto) {
        Questionnaire questionnaire = new Questionnaire();
        BeanUtils.copyProperties(dto, questionnaire);
        if (questionnaire.getStatus() == null) {
            questionnaire.setStatus("DRAFT");
        }
        assessmentMapper.insertQuestionnaire(questionnaire);
    }

    @Override
    public void updateQuestionnaire(Long id, QuestionnaireDTO dto) {
        if (id == null || id < 0) {
            throw new BizException("问卷id不合法");
        }
        Questionnaire questionnaire = new Questionnaire();
        BeanUtils.copyProperties(dto, questionnaire);
        questionnaire.setId(id);
        int updated = assessmentMapper.updateQuestionnaire(questionnaire);
        if (updated != 1) {
            throw new BizException("问卷不存在");
        }
    }

    @Override
    public void deleteQuestionnaire(Long id) {
        if (id == null || id < 0) {
            throw new BizException("问卷id不合法");
        }
        int updated = assessmentMapper.deleteQuestionnaire(id);
        if (updated != 1) {
            throw new BizException("问卷不存在");
        }
    }

    @Override
    public void updateQuestionnaireStatus(Long id, String status) {
        if (id == null || id < 0) {
            throw new BizException("问卷id不合法");
        }
        if (status == null || status.isBlank()) {
            throw new BizException("状态不能为空");
        }
        int updated = assessmentMapper.updateQuestionnaireStatus(id, status);
        if (updated != 1) {
            throw new BizException("问卷不存在");
        }
    }

    @Override
    public List<Question> questionList(Long questionnaireId) {
        if (questionnaireId == null || questionnaireId < 0) {
            throw new BizException("问卷id不合法");
        }
        if (assessmentMapper.getQuestionnaireById(questionnaireId) == null) {
            throw new BizException("问卷不存在");
        }
        return assessmentMapper.getQuestions(questionnaireId);
    }

    @Override
    public void createQuestion(Long questionnaireId, QuestionDTO dto) {
        if (questionnaireId == null || questionnaireId < 0) {
            throw new BizException("问卷id不合法");
        }
        if (assessmentMapper.getQuestionnaireById(questionnaireId) == null) {
            throw new BizException("问卷不存在");
        }
        validateOptions(dto.getType(), dto.getOptions());
        Question question = new Question();
        BeanUtils.copyProperties(dto, question);
        question.setQuestionnaireId(questionnaireId);
        assessmentMapper.insertQuestion(question);
    }

    @Override
    public void updateQuestion(Long id, QuestionDTO dto) {
        if (id == null || id < 0) {
            throw new BizException("题目id不合法");
        }
        validateOptions(dto.getType(), dto.getOptions());
        Question question = new Question();
        BeanUtils.copyProperties(dto, question);
        question.setId(id);
        int updated = assessmentMapper.updateQuestion(question);
        if (updated != 1) {
            throw new BizException("题目不存在");
        }
    }

    @Override
    public void deleteQuestion(Long id) {
        if (id == null || id < 0) {
            throw new BizException("题目id不合法");
        }
        int updated = assessmentMapper.deleteQuestion(id);
        if (updated != 1) {
            throw new BizException("题目不存在");
        }
    }

    /** 单选/多选题必须提供选项，文本题无需选项 */
    private void validateOptions(String type, String options) {
        if (("SINGLE".equals(type) || "MULTIPLE".equals(type)) && (options == null || options.isBlank())) {
            throw new BizException("单选/多选题必须提供选项");
        }
    }
}

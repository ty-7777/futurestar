package com.situ.futurestar.admin.service;

import com.situ.futurestar.core.dto.QuestionDTO;
import com.situ.futurestar.core.dto.QuestionnaireDTO;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.vo.PageResult;

import java.util.List;

public interface AdminAssessmentService {

    PageResult<Questionnaire> questionnaireList(int pageNum, int pageSize);

    void createQuestionnaire(QuestionnaireDTO dto);

    void updateQuestionnaire(Long id, QuestionnaireDTO dto);

    void deleteQuestionnaire(Long id);

    void updateQuestionnaireStatus(Long id, String status);

    List<Question> questionList(Long questionnaireId);

    void createQuestion(Long questionnaireId, QuestionDTO dto);

    void updateQuestion(Long id, QuestionDTO dto);

    void deleteQuestion(Long id);
}

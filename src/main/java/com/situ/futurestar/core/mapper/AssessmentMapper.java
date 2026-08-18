package com.situ.futurestar.core.mapper;

import com.situ.futurestar.core.entity.AssessmentResult;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssessmentMapper {
    // ---------- 会员端 ----------
    List<Questionnaire> getList();

    List<Question> getQuestions(Long id);

    void saveAssessment(AssessmentResult result);

    List<AssessmentResult> selectByUserId(Long userId);

    AssessmentResult selectById(Long id);

    // ---------- 管理端 ----------
    List<Questionnaire> listAllQuestionnaires();

    Questionnaire getQuestionnaireById(Long id);

    int insertQuestionnaire(Questionnaire questionnaire);

    int updateQuestionnaire(Questionnaire questionnaire);

    int deleteQuestionnaire(Long id);

    int updateQuestionnaireStatus(@Param("id") Long id, @Param("status") String status);

    int insertQuestion(Question question);

    int updateQuestion(Question question);

    int deleteQuestion(Long id);
}

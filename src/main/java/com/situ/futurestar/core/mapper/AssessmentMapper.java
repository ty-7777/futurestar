package com.situ.futurestar.core.mapper;


import com.situ.futurestar.core.entity.AssessmentResult;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AssessmentMapper {
    List<Questionnaire> getList();

    List<Question> getQuestions(Long id);

    void saveAssessment(AssessmentResult result);

    List<AssessmentResult> selectByUserId(Long userId);

    AssessmentResult selectById(Long id);
}

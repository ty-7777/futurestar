package com.situ.futurestar.api.service;

import com.situ.futurestar.core.dto.SubmitAssessmentDTO;
import com.situ.futurestar.core.entity.Question;
import com.situ.futurestar.core.entity.Questionnaire;
import com.situ.futurestar.core.vo.AssessmentResultVO;
import com.situ.futurestar.core.vo.PageResult;

import java.util.List;

public interface AssessmentService {
    List<Questionnaire> getList();

    List<Question> getQuestions(Long id);

    AssessmentResultVO submit(SubmitAssessmentDTO submitAssessmentDTO);

    PageResult<AssessmentResultVO> getHistory(int pageNum, int pageSize);

    AssessmentResultVO getDetail(Long id);
}

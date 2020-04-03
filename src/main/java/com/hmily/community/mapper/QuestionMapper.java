package com.hmily.community.mapper;

import com.hmily.community.domain.Question;
import com.hmily.community.dto.QuestionDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {
    void insertQuestion(Question question);

    List<QuestionDTO> getQuestionList();
}
package com.hmily.community.service;

import com.hmily.community.domain.Comment;
import com.hmily.community.dto.CommentDTO;

import java.util.List;

/**
 * @Date 2020/4/8 下午5:54
 * @Created by zhaoli
 */
public interface CommentService {
    void insertComment(Comment comment);

    List<CommentDTO> getCommentDTOByQuestionId(Integer id);
}

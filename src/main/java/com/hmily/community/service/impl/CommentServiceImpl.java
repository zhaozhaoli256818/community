package com.hmily.community.service.impl;

import com.hmily.community.domain.Comment;
import com.hmily.community.domain.Notification;
import com.hmily.community.domain.Question;
import com.hmily.community.dto.CommentDTO;
import com.hmily.community.enums.CommentTypeEnum;
import com.hmily.community.enums.NotificationStatusEnum;
import com.hmily.community.enums.NotificationTypeEnum;
import com.hmily.community.exception.CustomizeErrorCode;
import com.hmily.community.exception.CustomizeException;
import com.hmily.community.mapper.CommentMapper;
import com.hmily.community.mapper.NotificationMapper;
import com.hmily.community.mapper.QuestionMapper;
import com.hmily.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Date 2020/4/8 下午5:55
 * @Created by zhaoli
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private NotificationMapper notificationMapper;
    @Override
    @Transactional(rollbackFor = CustomizeException.class)
    public void insertComment(Comment comment) {
        if(comment.getParentId() == null || comment.getParentId() ==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FONUF);
        }
        if(comment.getType() == null || !CommentTypeEnum.isExsit(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            //回复评论
           Comment dbComment =  commentMapper.selectCommentById(comment.getParentId());
           if(dbComment == null){
               throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
           }
           commentMapper.insertComment(comment);
           createNotify(comment,dbComment.getCommentator(),NotificationTypeEnum.REPLY_COMMNET);
        }else{
            //回复问题
            Question question = questionMapper.getQuestionById(comment.getParentId());
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QEUSTION_NOT_FONUF);
            }
            commentMapper.insertComment(comment);
            questionMapper.addCommentCount(question.getId());
            createNotify(comment,question.getCreator(),NotificationTypeEnum.REPLY_QUESTION);
        }

    }

    public void createNotify(Comment comment , Integer receiver, NotificationTypeEnum typeEnum){
        if (comment.getCommentator() == receiver){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(comment.getGmtCreate());
        notification.setNotifier(comment.getCommentator());
        notification.setReceiver(receiver);
        notification.setType(typeEnum.getType());
        notification.setOuterId(comment.getParentId());
        notification.setStatus(NotificationStatusEnum.NOTIFICATION_UNREAD.getStatus());
        notificationMapper.insert(notification);
    }

    @Override
    public List<CommentDTO> getCommentDTOByTargetId(Integer id, CommentTypeEnum type) {
        return commentMapper.getCommentDTOByTargetId(id,type.getType());
    }

    @Override
    public Long getCommentCountById(Integer id) {
        return commentMapper.getCommentCountById(id);
    }

    @Override
    public Comment getCommentById(Integer outerId) {
        return commentMapper.getCommentById(outerId);
    }
}

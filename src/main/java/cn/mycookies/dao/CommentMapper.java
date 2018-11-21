package cn.mycookies.dao;

import cn.mycookies.pojo.po.Comment;
import cn.mycookies.pojo.vo.CommentVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {
    int insert(Comment record);

    int insertSelective(Comment record);

    int deleteComment(Integer commentId);

    int updateComment(Comment comment);

    Comment selectCommentById(Integer commentId);

    List<CommentVO> selectComments(Comment comment);

    int likeComment(Integer id);
}
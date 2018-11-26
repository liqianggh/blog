package cn.mycookies.dao;

import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.vo.CommentVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  CommentMapper {
    /**
     * 添加评论
     * @param record
     * @return
     */
    int insert(CommentDO record);

    /**
     * 添加评论
     * @param record
     * @return
     */
    int insertSelective(CommentDO record);

    /**
     * 删除评论 根据主键
     * @param commentId
     * @return
     */
    int deleteComment(Integer commentId);

    /**
     * 更新评论，做逻辑删除
     * @param commentDO
     * @return
     */
    int updateComment(CommentDO commentDO);

    /**
     * 查询单个评论信息
     * @param commentId
     * @return
     */
    CommentDO selectCommentById(Integer commentId);

    /**
     * 获取评论列表
     * @param commentDO
     * @return
     */
    List<CommentVO> selectComments(CommentDO commentDO);

    /**
     * 评论点赞
     * @param id
     * @return
     */
    int likeComment(Integer id);
}
package cn.mycookies.dao;

import cn.mycookies.pojo.dto.UserDTO;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.po.CommentDOExample;
import cn.mycookies.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface CommentDOMapper {
    long countByExample(CommentDOExample example);

    int deleteByExample(CommentDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CommentDO record);

    int insertSelective(CommentDO record);

    List<CommentDO> selectByExample(CommentDOExample example);

    CommentDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") CommentDO record, @Param("example") CommentDOExample example);

    int updateByExample(@Param("record") CommentDO record, @Param("example") CommentDOExample example);

    int updateByPrimaryKeySelective(CommentDO record);

    int updateByPrimaryKey(CommentDO record);


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

    UserDTO getUserByEmail(String email);

    int insertUser(UserDTO userDTO);
}
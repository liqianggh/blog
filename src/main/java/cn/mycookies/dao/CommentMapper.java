package cn.mycookies.dao;

import cn.mycookies.pojo.po.Comment;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper {
    int insert(Comment record);

    int insertSelective(Comment record);
}
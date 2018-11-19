package cn.mycookies.dao;

import cn.mycookies.pojo.po.Comment;

public interface CommentMapper {
    int insert(Comment record);

    int insertSelective(Comment record);

}
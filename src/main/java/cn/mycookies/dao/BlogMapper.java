package cn.mycookies.dao;

import cn.mycookies.pojo.po.Blog;

public interface BlogMapper {
    int insert(Blog record);

    int insertSelective(Blog record);
}
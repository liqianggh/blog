package cn.mycookies.dao;

import cn.mycookies.pojo.po.Blog;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogMapper {
    int insert(Blog record);

    int insertSelective(Blog record);
}
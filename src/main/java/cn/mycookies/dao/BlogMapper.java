package cn.mycookies.dao;

import cn.mycookies.pojo.po.Blog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogMapper {
    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectById(@Param("id") Integer id, @Param("isDeleted") Byte isDeleted);

    List<Blog> selectBlogs(@Param("categoryId") Byte categoryId, @Param("tagId")Byte tagId, @Param("isDeleted")Integer isDeleted);
}
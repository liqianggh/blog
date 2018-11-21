package cn.mycookies.dao;

import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.po.Blog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogMapper {
    int insert(Blog record);

    int insertSelective(BlogDTO record);

    Blog selectById(@Param("id") Integer id, @Param("isDeleted") Byte isDeleted);

    List<Blog> selectBlogs(@Param("categoryId") Integer categoryId, @Param("tagId")Integer tagId, @Param("isDeleted")Byte isDeleted);

    int updateBlog(BlogDTO blogDTO);
    /**
     * 上一条-1 或者 下一条1
     * @param id
     * @return
     */
    Blog selectLastOrNext(@Param("id")Integer id,@Param("page")Integer page);


    int updateBlogCount(@Param("blogId") Integer id, @Param("type") String type);
}
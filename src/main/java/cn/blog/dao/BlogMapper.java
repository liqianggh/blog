package cn.blog.dao;

import cn.blog.bo.BlogBo;
import cn.blog.bo.TagsAndBlog;
import cn.blog.pojo.Blog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface BlogMapper {
    int deleteByPrimaryKey(Integer blogid);

    int insert(Blog record);

    int insertSelective(Blog record);

    Blog selectByPrimaryKey(Integer blogid);

    int updateByPrimaryKeySelective(Blog record);

    int updateByPrimaryKeyWithBLOBs(Blog record);

    int updateByPrimaryKey(Blog record);

    List<BlogBo> selectByCodeTitleTagCategory(@Param("code") Integer code, @Param("title") String title, @Param("tagId") Integer tagId, @Param("categoryId") Integer categoryId);

    Blog selectTagsByblogId(Integer blogId);

    int selectCount(Integer blogId);

    TagsAndBlog selectTagAndBlog(@Param("blogId") Integer blogId, @Param("tagId") Integer tagId);

    int addTagToBlog(@Param("blogId") Integer blogId, @Param("tagId") Integer tagId);

    BlogBo selectBoById(Integer blogId);

    int updateByPrimaryKeySelectiveWithBlobs(Blog blog);

    Blog selectByPrimaryKeyWithBlobs(Integer blogId);

    BlogBo selectBoByIdWithBlobs(Integer blogId);

    BlogBo selectBoByIdWithBlobsNoSummary(Integer blogId);

    int addTagsToBlog(@Param("blogId") Integer blogId, @Param("idList") List<Integer> idList);

    List<BlogBo> selectTheSameTagByBlogId(Integer blogId);

    List<BlogBo> selectSameCategoryByBlogId(Integer blogId);

    BlogBo selectLastById(Integer blogId);

    BlogBo selectNextById(Integer blogId);

    int cancelLike(Integer blogId);
    int addLike(Integer blogId);

    int addViewCount(Integer blogId);
}
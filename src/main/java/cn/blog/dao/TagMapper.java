package cn.blog.dao;

import cn.blog.pojo.Tag;
import cn.blog.vo.TagVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(Integer tagid);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Integer tagid);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    List<Tag> findALl();

    List<TagVo> findAllSimple();

//    int insertBatch(TagsAndBlog tagsAndBlog);

    List<String> findByIds(@Param("tagIdList")List<Integer> tagIdList);

    int selectCount(Integer tagId);

    List<TagVo> findALlWithCount();

    int selectCountOfTagsAndBlog(@Param("tagId") Integer tagId, @Param("blogId") Integer blogId);

    List<Tag> selectTagsOfBlog(Integer blogId);

    int deleteBlogTags(Integer blogId);
}
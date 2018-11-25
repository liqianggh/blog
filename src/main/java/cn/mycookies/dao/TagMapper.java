package cn.mycookies.dao;

import cn.mycookies.pojo.dto.TagAdd;
import cn.mycookies.pojo.po.Tag;
import cn.mycookies.pojo.vo.TagVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper {
    int insert(TagAdd record);

    int insertSelective(Tag record);

    List<Tag> queryTagList(Tag tag);

    /**
     * 根据name查询，判断是否存在
     * @return
     */
    Tag queryByName(Tag tag);

    Integer updateTag(Tag tag);

    Tag queryById(Tag tag);

    int deleteById(Tag tag);

    List<TagVO> queryTagBoList();

    List<TagVO> queryTagVOList();

    List<TagVO> queryTagsOfBlog(Integer blogId);

    List<TagVO> queryCategoryVOList();
}
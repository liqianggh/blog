package cn.mycookies.dao;

import cn.mycookies.pojo.bo.TagAdd;
import cn.mycookies.pojo.po.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper {
    int insert(TagAdd record);

    int insertSelective(Tag record);

    List<Tag> queryTagList();

    /**
     * 根据name查询，判断是否存在
     * @param tagName
     * @return
     */
    Tag queryByName(String tagName);

    Integer updateTag(Tag tag);

    Tag queryById(Integer id);

    int deleteById(Integer id);
}
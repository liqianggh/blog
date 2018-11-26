package cn.mycookies.dao;

import cn.mycookies.pojo.dto.TagAddDTO;
import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.vo.TagVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper {
    int insert(TagAddDTO record);

    int insertSelective(TagDO record);

    List<TagDO> queryTagList(TagDO tagDO);

    /**
     * 根据name查询，判断是否存在
     * @return
     */
    TagDO queryByName(TagDO tagDO);

    Integer updateTag(TagDO tagDO);

    TagDO queryById(TagDO tagDO);

    int deleteById(TagDO tagDO);

    List<TagVO> queryTagBoList();

    List<TagVO> queryTagVOList();

    List<TagVO> queryTagsOfBlog(Integer blogId);

    List<TagVO> queryCategoryVOList();
}
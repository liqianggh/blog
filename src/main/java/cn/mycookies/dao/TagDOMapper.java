package cn.mycookies.dao;

import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.po.TagDOExample;
import cn.mycookies.pojo.vo.TagVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagDOMapper {
    long countByExample(TagDOExample example);

    int deleteByExample(TagDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TagDO record);

    int insertSelective(TagDO record);

    List<TagDO> selectByExample(TagDOExample example);

    TagDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TagDO record, @Param("example") TagDOExample example);

    int updateByExample(@Param("record") TagDO record, @Param("example") TagDOExample example);

    int updateByPrimaryKeySelective(TagDO record);

    int updateByPrimaryKey(TagDO record);

    /**-------------------------------------------*/
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

    List<TagVO> queryTagsOfBlog(Integer blogId);

    List<TagVO> queryCategoryVOList();
}
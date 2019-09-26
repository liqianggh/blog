package cn.mycookies.dao;

import cn.mycookies.pojo.dto.TagWithCountVO;
import cn.mycookies.pojo.meta.TagDO;
import cn.mycookies.pojo.meta.TagDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface TagMapper {
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
    /**
     * 根据name查询，判断是否存在
     * @return
     */
    TagDO queryByName(TagDO TagDO);

    Integer updateTag(TagDO TagDO);

    TagDO queryById(TagDO TagDO);

    int deleteById(TagDO TagDO);

    List<TagWithCountVO> selectTagList();

    List<TagWithCountVO> selectCategoryList();

    List<TagDO> selectTagsOfBlog(Integer blogId);
}
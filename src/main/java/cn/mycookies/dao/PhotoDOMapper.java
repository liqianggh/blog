package cn.mycookies.dao;

import cn.mycookies.pojo.meta.PhotoDO;
import cn.mycookies.pojo.meta.PhotoDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PhotoDOMapper {
    long countByExample(PhotoDOExample example);

    int deleteByExample(PhotoDOExample example);

    int deleteByPrimaryKey(Short id);

    int insert(PhotoDO record);

    int insertSelective(PhotoDO record);

    List<PhotoDO> selectByExample(PhotoDOExample example);

    PhotoDO selectByPrimaryKey(Short id);

    int updateByExampleSelective(@Param("record") PhotoDO record, @Param("example") PhotoDOExample example);

    int updateByExample(@Param("record") PhotoDO record, @Param("example") PhotoDOExample example);

    int updateByPrimaryKeySelective(PhotoDO record);

    int updateByPrimaryKey(PhotoDO record);
}
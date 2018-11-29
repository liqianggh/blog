package cn.mycookies.dao;

import cn.mycookies.pojo.po.PhotoDO;

public interface PhotoDOMapper {
    int deleteByPrimaryKey(Short id);

    int insert(PhotoDO record);

    int insertSelective(PhotoDO record);

    PhotoDO selectByPrimaryKey(Short id);

    int updateByPrimaryKeySelective(PhotoDO record);

    int updateByPrimaryKey(PhotoDO record);
}
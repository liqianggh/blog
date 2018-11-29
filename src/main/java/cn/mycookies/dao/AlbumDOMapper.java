package cn.mycookies.dao;

import cn.mycookies.pojo.po.AlbumDO;

public interface AlbumDOMapper {
    int deleteByPrimaryKey(Short id);

    int insert(AlbumDO record);

    int insertSelective(AlbumDO record);

    AlbumDO selectByPrimaryKey(Short id);

    int updateByPrimaryKeySelective(AlbumDO record);

    int updateByPrimaryKey(AlbumDO record);
}
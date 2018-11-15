package cn.mycookies.dao;

import cn.mycookies.pojo.po.Tag;

public interface TagMapper {
    int insert(Tag record);

    int insertSelective(Tag record);
}
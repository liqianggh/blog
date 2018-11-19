package cn.mycookies.dao;

import cn.mycookies.pojo.po.Category;

public interface CategoryMapper {
    int insert(Category record);

    int insertSelective(Category record);
}
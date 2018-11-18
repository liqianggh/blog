package cn.mycookies.dao;

import cn.mycookies.pojo.po.Category;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryMapper {
    int insert(Category record);

    int insertSelective(Category record);
}
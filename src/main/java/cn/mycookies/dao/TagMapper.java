package cn.mycookies.dao;

import cn.mycookies.pojo.po.Tag;
import org.springframework.stereotype.Repository;

@Repository
public interface TagMapper {
    int insert(Tag record);

    int insertSelective(Tag record);
}
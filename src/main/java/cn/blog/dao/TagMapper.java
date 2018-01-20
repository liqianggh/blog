package cn.blog.dao;

import cn.blog.pojo.Tag;
import cn.blog.vo.TagVo;

import java.util.List;

public interface TagMapper {
    int deleteByPrimaryKey(Integer tagid);

    int insert(Tag record);

    int insertSelective(Tag record);

    Tag selectByPrimaryKey(Integer tagid);

    int updateByPrimaryKeySelective(Tag record);

    int updateByPrimaryKey(Tag record);

    List<Tag> findALl();

    List<TagVo> findAllSimple();
}
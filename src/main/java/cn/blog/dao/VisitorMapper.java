package cn.blog.dao;

import cn.blog.pojo.Visitor;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface VisitorMapper {
    int deleteByPrimaryKey(Integer visitorid);

    int insert(Visitor record);

    int insertSelective(Visitor record);

    Visitor selectByPrimaryKey(Integer visitorid);

    int updateByPrimaryKeySelective(Visitor record);

    int updateByPrimaryKey(Visitor record);

    int batchInsert(@Param("visitors") List<Visitor> visitors);
}
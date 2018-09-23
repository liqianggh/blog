package cn.mycookies.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface HelloDao {
    @Select("select tagName from tag")
    public List<String> allUsers();
}

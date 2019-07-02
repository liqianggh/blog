package cn.mycookies.dao;

import cn.mycookies.pojo.po.VisitorDO;
import cn.mycookies.pojo.po.VisitorDOExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VisitorDOMapper {
    long countByExample(VisitorDOExample example);

    int deleteByExample(VisitorDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(VisitorDO record);

    int insertSelective(VisitorDO record);

    List<VisitorDO> selectByExample(VisitorDOExample example);

    VisitorDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") VisitorDO record, @Param("example") VisitorDOExample example);

    int updateByExample(@Param("record") VisitorDO record, @Param("example") VisitorDOExample example);

    int updateByPrimaryKeySelective(VisitorDO record);

    int updateByPrimaryKey(VisitorDO record);
}
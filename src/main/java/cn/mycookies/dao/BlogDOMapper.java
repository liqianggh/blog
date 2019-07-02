package cn.mycookies.dao;

import cn.mycookies.pojo.dto.BlogAddRequest;
import cn.mycookies.pojo.dto.BlogListRequest;
import cn.mycookies.pojo.po.BlogDO;
import cn.mycookies.pojo.po.BlogDOExample;
import cn.mycookies.pojo.vo.BlogVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BlogDOMapper {

    long countByExample(BlogDOExample example);

    int deleteByExample(BlogDOExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BlogDO record);

    int insertSelective(BlogDO record);

    List<BlogDO> selectByExampleWithBLOBs(BlogDOExample example);

    List<BlogDO> selectByExample(BlogDOExample example);

    BlogDO selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BlogDO record, @Param("example") BlogDOExample example);

    int updateByExampleWithBLOBs(@Param("record") BlogDO record, @Param("example") BlogDOExample example);

    int updateByExample(@Param("record") BlogDO record, @Param("example") BlogDOExample example);

    int updateByPrimaryKeySelective(BlogDO record);

    int updateByPrimaryKeyWithBLOBs(BlogDO record);

    int updateByPrimaryKey(BlogDO record);


    BlogDO selectByIdAndStatus(@Param("id") Integer id, @Param("blogStatus") Byte blogStatus);

    List<BlogDO> selectBlogs(BlogListRequest blogListRequest);

    List<BlogVO> selectHotOrRecommendBlogs(@Param("code") int code, @Param("limit") int limit);

    int updateBlog(BlogAddRequest blogAddRequest);

    /**
     * 上一条-1 或者 下一条1
     * @param id
     * @return
     */
    BlogDO selectLastOrNext(@Param("id")Integer id, @Param("isLast")boolean isLast);


    int updateBlogCount(@Param("blogId") Integer id, @Param("type") String type);
}

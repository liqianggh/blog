package cn.blog.service;

import cn.blog.bo.TagsAndBlog;
import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import cn.blog.vo.BlogVo;
import cn.blog.vo.IndexVo;
import cn.blog.vo.TagVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IBlogService {
    ServerResponse saveOrUpdate(Blog blog,String tagIds);

    ServerResponse<PageInfo> listByCodeTitleTagCategory(Integer code, String title,String orderBy, Integer tagId, Integer categoryId, int pageNum, int pageSize);

    ServerResponse addTagToBlog(Integer blogId,Integer  tagId);

    PageInfo findBlogVoPageInfo(Integer code,Integer categoryId,Integer tagId,String orderBy,String title,Integer pageNum,Integer pageSize);

    List<BlogVo> findBlogVoList(Integer code,Integer categoryId,Integer tagId,String orderBy,String title,Integer pageNum,Integer pageSize);

    BlogVo  descVo(Integer blogId);

    boolean isExists(Integer blogId);
    List<BlogVo> guessYouLike(Integer blogId);

     BlogVo  findLast(Integer blogId);
     BlogVo  findNext(Integer blogId);

}

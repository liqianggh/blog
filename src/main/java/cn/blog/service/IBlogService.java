package cn.blog.service;

import cn.blog.bo.TagsAndBlog;
import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IBlogService {
    ServerResponse saveOrUpdate(Blog blog);

    ServerResponse<PageInfo> listByCodeTitleTagCategory(Integer code, String title, Integer tagId, Integer categoryId, int pageNum, int pageSize);

    ServerResponse addTagsToBlog(Integer blogId,Integer  tagId);
}

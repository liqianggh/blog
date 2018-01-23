package cn.blog.service;

import cn.blog.bo.TagsAndBlog;
import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import cn.blog.vo.IndexVo;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IBlogService {
    ServerResponse saveOrUpdate(Blog blog,String tagIds);

    ServerResponse<PageInfo> listByCodeTitleTagCategory(Integer code, String title,String orderBy, Integer tagId, Integer categoryId, int pageNum, int pageSize);

    ServerResponse addTagToBlog(Integer blogId,Integer  tagId);
    //首页初始化
    ServerResponse<IndexVo> indexInitial();
}

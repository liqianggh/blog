package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import org.springframework.stereotype.Service;

public interface IBlogService {
    ServerResponse saveOrUpdate(Blog blog);

}

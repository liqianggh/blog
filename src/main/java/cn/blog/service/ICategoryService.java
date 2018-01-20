package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.vo.CategoryVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ICategoryService {

    ServerResponse saveOrUpdate(Category category);

    ServerResponse delete(Integer categoryId);

    ServerResponse<PageInfo> listAllSimple(Integer pageNum,Integer pageSize);

    ServerResponse<PageInfo> listAll(Integer pageNum, Integer pageSize);

    ServerResponse<CategoryVo> findById(Integer categoryId);

}

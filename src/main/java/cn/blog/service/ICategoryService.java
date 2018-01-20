package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.vo.CategoryVo;

import java.util.List;

public interface ICategoryService {

    ServerResponse saveOrUpdate(Category category);

    ServerResponse delete(Integer categoryId);

    ServerResponse<List<CategoryVo>> listAllSimple();

    ServerResponse<List<CategoryVo>> listAll();

    ServerResponse<CategoryVo> findById(Integer categoryId);

}

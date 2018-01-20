package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;

import java.util.List;

public interface ICategoryService {

    ServerResponse saveOrUpdate(Category category);

    ServerResponse delete(Integer categoryId);

    ServerResponse<List<Category>> listAllSimple();

    ServerResponse<List<Category>> listAll();

    ServerResponse<Category> findById(Integer categoryId);

}

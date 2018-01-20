package cn.blog.service.impl;

import cn.blog.common.ResponseCode;
import cn.blog.common.ServerResponse;
import cn.blog.dao.CategoryMapper;
import cn.blog.pojo.Category;
import cn.blog.service.ICategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.blog.common.ServerResponse.createBySuccess;

/**
  * @Description: 博客分类的业务逻辑
  * Created by Jann Lee on 2018/1/20  14:37.
  */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    private CategoryMapper categoryMapper;
     @Override
     public ServerResponse saveOrUpdate(Category category) {
         //校验数据是否为空
         if(category==null){
             return  ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
         }
         if(StringUtils.isEmpty(category.getCategoryName())){
             return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
         }
         int rowCount=0;
         if(category.getCategoryId()==null){
             //新增
             rowCount = categoryMapper.insertSelective(category);

         }else{
             //更新
             rowCount=categoryMapper.updateByPrimaryKeySelective(category);
         }
        if(rowCount>0){
             return createBySuccess();
        }
         return ServerResponse.createByErrorMessage("修改/新增失败！");
     }

     @Override
     public ServerResponse delete(Integer categoryId) {
         if(categoryId==null){
             return  ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
         }
         //存在性检验
         Category result = categoryMapper.selectByPrimaryKey(categoryId);
         if(result==null){
             return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
         }
         int rowCount = categoryMapper.deleteByPrimaryKey(categoryId);

         if(rowCount>0){
             return createBySuccess();
         }
         return ServerResponse.createByErrorMessage("删除失败！");
     }

    /**
     * 查找简单的categoryName和id
     * @return
     */
     @Override
     public ServerResponse<List<Category>> listAllSimple() {
         List<Category> categoryList = categoryMapper.selectAllSimple();
         return createBySuccess(categoryList);
     }
    /**
     * 查找完整的cagegory
     * @return
     */
    @Override
    public ServerResponse<List<Category>> listAll() {
        List<Category> categoryList = categoryMapper.selectAllSimple();
        return createBySuccess(categoryList);
    }

    @Override
    public ServerResponse<Category> findById(Integer categoryId) {
        if(categoryId==null){
            return  ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        //存在性检验
        Category result = categoryMapper.selectByPrimaryKey(categoryId);
        if(result==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }
        return ServerResponse.createBySuccess(result);
    }
}

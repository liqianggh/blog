package cn.blog.controller.backend;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
  * @Description: 后台 分类的Controller
  * Created by Jann Lee on 2018/1/20  15:53.
  */
@Controller
@RequestMapping("/manage/category")
public class CategoryController {

     @Autowired
     ICategoryService iCategoryService;

     @RequestMapping("saveOrUpdate.do")
     @ResponseBody
     public ServerResponse saveOrUpdate(Category category){
         return iCategoryService.saveOrUpdate(category);
     }

     @RequestMapping("listAll.do")
     @ResponseBody
     public ServerResponse listAll(){
         return iCategoryService.listAll();
     }

     @RequestMapping("listAllSimple.do")
     @ResponseBody
     public ServerResponse listAllSimple(){
         return iCategoryService.listAllSimple();
     }

     @RequestMapping("delete.do")
     @ResponseBody
     public ServerResponse listAll(Integer categoryId){
         return iCategoryService.delete(categoryId);
     }

 }

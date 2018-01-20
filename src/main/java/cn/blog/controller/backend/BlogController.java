package cn.blog.controller.backend;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import cn.blog.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

 /**
  * @Description:  后台 博客contrller
  * Created by Jann Lee on 2018/1/20  0:34.
  */
@Controller
@RequestMapping("/manage/blog")
public class BlogController {

    @Autowired
    private IBlogService iBlogService;

     /**
      * 新增博客
      * @param blog
      * @return
      */
    @RequestMapping(value="add.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(Blog blog){
        return iBlogService.saveOrUpdate(blog);
    }


}

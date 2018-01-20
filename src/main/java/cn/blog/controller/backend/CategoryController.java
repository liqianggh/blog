package cn.blog.controller.backend;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.service.ICategoryService;
import cn.blog.vo.CategoryVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    public ServerResponse saveOrUpdate(Category category) {
        return iCategoryService.saveOrUpdate(category);
    }

    @RequestMapping("listAll.do")
    @ResponseBody
    public ServerResponse<PageInfo> listAll(@RequestParam(value="pageNum",defaultValue = "1")Integer pageNum,
                                                        @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize) {
        return iCategoryService.listAll(pageNum,pageSize);
    }

    @RequestMapping("listAllSimple.do")
    @ResponseBody
    public ServerResponse<PageInfo> listAllSimple(@RequestParam(value="pageNum",defaultValue = "1")Integer pageNum,
                                        @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize) {
        return iCategoryService.listAllSimple(pageNum,pageSize);
    }

    @RequestMapping("delete.do")
    @ResponseBody
    public ServerResponse delete(Integer categoryId) {
        return iCategoryService.delete(categoryId);
    }

}

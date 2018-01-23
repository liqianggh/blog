package cn.blog.controller.potral;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import cn.blog.service.IBlogService;
import cn.blog.service.ITagService;
import cn.blog.vo.IndexVo;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: 用户 博客contrller
 * Created by Jann Lee on 2018/1/22  19:34.
 */
@Controller
@RequestMapping("/user/blog")
@Slf4j
public class UserBlogController {

    @Autowired
    private IBlogService iBlogService;

    /**
     * 高复用查询博客列表,
     * @param code       博客的类型（私有1,公开0,推荐2）
     * @param title      博客标题 模糊匹配
     * @param tagId      标签id
     * @param categoryId 分类id
     * @return ServerResponse
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(Integer code,
                                         String title,
                                         Integer tagId,
                                         Integer categoryId,
                                         @RequestParam(value="orderBy" ,required = false)String orderBy,
                                         @RequestParam(value="pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        return  iBlogService.listByCodeTitleTagCategory(code,title,orderBy,tagId,categoryId,pageNum,pageSize);
    }


     /**
      * @Description: 首页初始化
      * Created by Jann Lee on 2018/1/23  19:59.
      */
     @RequestMapping("load_index.do")
     @ResponseBody
     public ServerResponse<IndexVo> loadIndex() {

         return  iBlogService.indexInitial();
     }


}

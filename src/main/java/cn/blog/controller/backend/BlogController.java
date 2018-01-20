package cn.blog.controller.backend;

import cn.blog.common.Const;
import cn.blog.common.ServerResponse;
import cn.blog.pojo.Blog;
import cn.blog.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static cn.blog.common.Const.BlogCodeType.PUBLIC;

/**
 * @Description: 后台 博客contrller
 * Created by Jann Lee on 2018/1/20  0:34.
 */
@Controller
@RequestMapping("/manage/blog")
public class BlogController {

    @Autowired
    private IBlogService iBlogService;

    /**
     * 新增/修改博客
     * @param   blog{title,content,categoryId,
     *              [id],[createTime],[code],[author],
     *              [viewCount],[likeCount],[shareCount],
     *              [commentCount],[updateTime],[tags],[imgUri])}
     * @return
     */
    @RequestMapping(value = "add.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse add(Blog blog) {
        return iBlogService.saveOrUpdate(blog);
    }

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
    public ServerResponse list(@RequestParam(value="code",defaultValue = ""+Const.BlogCodeType.PUBLIC+"") Integer code,
                               String title,
                               String tagId,
                               String categoryId,
                               @RequestParam(value="pageNum",defaultValue = "1")int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSize) {
        return iBlogService.saveOrUpdate(null);
    }


}

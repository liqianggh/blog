package cn.blog.controller.potral;

import cn.blog.common.Const;
import cn.blog.common.ResponseCode;
import cn.blog.common.ServerResponse;
import cn.blog.service.IBlogService;
import cn.blog.service.ICategoryService;
import cn.blog.service.ITagService;
import cn.blog.vo.*;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

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
    @Autowired
    private ITagService iTagService;
    @Autowired
    private ICategoryService iCategoryService;

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
     public ServerResponse<IndexVo> loadIndex(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value="pageSize",defaultValue = "10")Integer pageSize) {
         IndexVo indexVo = new IndexVo();
         List<BlogVo> hotBlog = iBlogService.findBlogVoList(1,null,null,"viewCount desc",null,1,Const.IndexConst.HOT_NUM);
         PageInfo pageInfo = iBlogService.findBlogVoPageInfo(1,null,null,"createTime desc",null,1,Const.IndexConst.BLOG_NUM);
         List<BlogVo> recommendedBlog = iBlogService.findBlogVoList(2,null,null,"createTime desc",null,1,Const.IndexConst.RECOMMENDED);
         List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
         List<CategoryVo> categoryVoList = iCategoryService.findAllWithCount();
         indexVo.setTagVoList(tagVoList);
         indexVo.setRecommendBlog(recommendedBlog);
         indexVo.setHotBlogs(hotBlog);
         indexVo.setCategoryVoList(categoryVoList);
         indexVo.setBlogPageInfo(pageInfo);
         return ServerResponse.createBySuccess(indexVo);
     }
     /**
      * @Param :blogId 文章id
      * @Description:  根据id查询文章信息，并且根据标签或者分类进行推荐
      * Created by Jann Lee on 2018/1/24  13:39.
      */
    @RequestMapping("article.do")
    @ResponseBody
    public ServerResponse<ArticleVo> detail(Integer blogId){
        if(blogId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode()
                    ,ResponseCode.NULL_ARGUMENT.getDesc());
        }
        boolean result = iBlogService.isExists(blogId);
        if(!result){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        ArticleVo articleVo = new ArticleVo();
        BlogVo  blogVo = iBlogService.descVo(blogId);
        List<BlogVo>  blogVoList = iBlogService.guessYouLike(blogId);

        articleVo.setBlogVo(blogVo);

        articleVo.setBlogVoList(blogVoList);
        return ServerResponse.createBySuccess(articleVo);
    }

    @RequestMapping("list_by_category.do")
    @ResponseBody
    public ServerResponse<PageInfo> listByCategory(Integer categoryId, @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        //todo 校验参数合法性
        if(categoryId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        ArticleVo articleVo = new ArticleVo();
       ServerResponse<PageInfo> blogVoList = iBlogService.listByCodeTitleTagCategory(
                Const.BlogCodeType.PUBLIC_BLOG,null,
                "createTime desc",null,categoryId,pageNum,pageSize);
        return blogVoList;

    }

    @RequestMapping("list_by_tag.do")
    @ResponseBody
    public ServerResponse<PageInfo> listByTag(Integer tagId, @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        //todo 校验参数合法性
        if(tagId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        ArticleVo articleVo = new ArticleVo();
        ServerResponse<PageInfo> blogVoList = iBlogService.listByCodeTitleTagCategory(
                Const.BlogCodeType.PUBLIC_BLOG,null,
                "createTime desc",tagId,null,pageNum,pageSize);
        return blogVoList;

    }




}

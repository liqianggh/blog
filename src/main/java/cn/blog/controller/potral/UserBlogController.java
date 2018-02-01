package cn.blog.controller.potral;

import cn.blog.bo.RemoteUser;
import cn.blog.common.Const;
import cn.blog.common.RedisPool;
import cn.blog.common.ResponseCode;
import cn.blog.common.ServerResponse;
import cn.blog.pojo.Category;
import cn.blog.pojo.Tag;
import cn.blog.service.CacheService.CacheService;
import cn.blog.service.IBlogService;
import cn.blog.service.ICategoryService;
import cn.blog.service.ITagService;
import cn.blog.util.JsonUtil;
import cn.blog.util.RedisPoolUtil;
import cn.blog.vo.*;
import com.github.pagehelper.PageInfo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private CacheService cacheService;

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
         List<BlogVo> hotBlog = iBlogService.findBlogVoList(1,null,null,"viewCount_desc",null,1,Const.IndexConst.HOT_NUM);
         PageInfo pageInfo = iBlogService.findBlogVoPageInfo(1,null,null,"createTime_desc",null,pageNum,Const.IndexConst.BLOG_NUM);
         List<BlogVo> recommendedBlog = iBlogService.findBlogVoList(2,null,null,"createTime_desc",null,1,Const.IndexConst.RECOMMENDED);
//         List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
//         List<TagVo> tagVoList = cacheService.findTagsWithCount();
//
//         List<CategoryVo> categoryVoList = iCategoryService.findAllWithCount();
         //使用缓存
         List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
         List<CategoryVo> categoryVoList = cacheService.findAllCategoryWithBlogCount();



         indexVo.setTagVoList(tagVoList);
         indexVo.setRecommendBlog(recommendedBlog);
         indexVo.setHotBlogs(hotBlog);
         indexVo.setCategoryVoList(categoryVoList);
         indexVo.setBlogPageInfo(pageInfo);
         return ServerResponse.createBySuccess(indexVo);
     }

     /**
      * @Description: 高可用的下一页
      * Created by Jann Lee on 2018/1/26  23:43.
      */
    @RequestMapping("next_page.do")
    @ResponseBody
    public ServerResponse<PageInfo> nextPage(Integer categoryId,Integer tagId,String title,@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value="pageSize",defaultValue = "10")Integer pageSize) {

        PageInfo pageInfo = iBlogService.findBlogVoPageInfo(1,categoryId,tagId,"createTime_desc",title,pageNum,Const.IndexConst.BLOG_NUM);

        return ServerResponse.createBySuccess(pageInfo);
    }


    @RequestMapping("loadAt_by_id.do")
    @ResponseBody
    public ServerResponse<ArticleVo> loadAtById(Integer blogId){
        ArticleVo articleVo = new ArticleVo();
        if(blogId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode()
                    ,ResponseCode.NULL_ARGUMENT.getDesc());
        }
        boolean result = iBlogService.isExists(blogId);
        if(!result){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        BlogVo blogVo = iBlogService.descVo(blogId);
        BlogVo nextBlog = iBlogService.findNext(blogId);
        BlogVo lastBlog = iBlogService.findLast(blogId);

        articleVo.setBlogVo(blogVo);

        articleVo.setNextBlog(nextBlog);
        articleVo.setLastBlog(lastBlog);

        return ServerResponse.createBySuccess(articleVo);
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
        //未使用缓存
//        List<TagVo> tagVoList = cacheService.findTagsWithCount();
//        List<CategoryVo>  categoryVoList = iCategoryService.findAllWithCount();
        //使用缓存
        List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
        List<CategoryVo> categoryVoList = cacheService.findAllCategoryWithBlogCount();


        //上一篇，下一篇
        BlogVo lastBlog = iBlogService.findLast(blogId);
        BlogVo nextBlog = iBlogService.findNext(blogId);

        articleVo.setBlogVo(blogVo);
        articleVo.setBlogVoList(blogVoList);
        articleVo.setTagVoList(tagVoList);
        articleVo.setCategoryList(categoryVoList);
        articleVo.setLastBlog(lastBlog);
        articleVo.setNextBlog(nextBlog);

        return ServerResponse.createBySuccess(articleVo);
    }

    @RequestMapping("list_by_category.do")
    @ResponseBody
    public ServerResponse<PageInfo> listByCategory(Integer categoryId,
                                                   @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
                                                   @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){

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
log.info("接收到的参数："+tagId);

        //todo 校验参数合法性
        if(tagId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        ArticleVo articleVo = new ArticleVo();
        ServerResponse<PageInfo> blogVoList = iBlogService.listByCodeTitleTagCategory(
                Const.BlogCodeType.PUBLIC_BLOG,null,
                "createTime_desc",tagId,null,pageNum,pageSize);
        return blogVoList;

    }
    @RequestMapping("category_tag_init.do")
    @ResponseBody
    public ServerResponse<IndexVo> categoryOrTagInit(Integer tagId,Integer categoryId, @RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value="pageSize",defaultValue = "10")Integer pageSize){
        //todo 校验参数合法性 优化
        if(tagId==null&&categoryId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        IndexVo indexVo = new IndexVo();
        PageInfo pageInfo ;
        if(tagId!=null){
            indexVo.setId(tagId);
            Tag tag  = iTagService.findTagById(tagId);
            if(tag==null){
                return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            indexVo.setName(tag.getTagName());
            indexVo.setIsCategory(0);
            pageInfo=iBlogService.findBlogVoPageInfo(1,null,tagId,"createTime_desc",null,pageNum,pageSize);
        }else{
            indexVo.setId(categoryId);
            Category category = iCategoryService.findSimpleCById(categoryId);
            if(category==null){
                return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            indexVo.setName(category.getCategoryName());
            indexVo.setIsCategory(1);
            pageInfo=iBlogService.findBlogVoPageInfo(1,categoryId,null,"createTime_desc",null,pageNum,pageSize);
        }
        //最新发布
        List<BlogVo> newPublishdBlog = iBlogService.findBlogVoList(1,null,null,"createTime_desc",null,1,Const.IndexConst.NEW_PUBLISH);


//        List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
//        List<CategoryVo> categoryVoList = iCategoryService.findAllWithCount();

        //使用缓存
        List<TagVo> tagVoList = iTagService.listAllSimpleWithCount();
        List<CategoryVo> categoryVoList = cacheService.findAllCategoryWithBlogCount();


        indexVo.setTagVoList(tagVoList);
        indexVo.setRecommendBlog(newPublishdBlog);
        indexVo.setCategoryVoList(categoryVoList);
        indexVo.setBlogPageInfo(pageInfo);

        return ServerResponse.createBySuccess(indexVo);
    }

    @RequestMapping("add_like.do")
    @ResponseBody
    public ServerResponse addLike(Integer blogId, HttpServletRequest request){

        if(blogId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        boolean flag  = iBlogService.isExists(blogId);
        if(!flag){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //获取请求的ip
        String remoteAddr = request.getRemoteAddr();
        //查询redis中是否有该ip
        Boolean isExistsLiker = cacheService.isLikeUserExists(remoteAddr,blogId);
        log.info(isExistsLiker+"this is result结果");
        String message = "";
        Boolean result;
        int code = 0;
        //如果没有点过赞 否则取消赞 然后记录到缓存中
        if(!isExistsLiker){
            //todo 判断是否添加到缓存
            //存入Redis
            RedisPoolUtil.hset(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS,remoteAddr+"_"+ blogId.toString(),Boolean.toString(true),Const.CacheTime.ADD_LIKE_TIME);
            //修改数据库中的数据
            result =iBlogService.addLike(blogId);
            message="点赞";
            code = ResponseCode.LIKE_IT_SUCCESS.getCode();

        }else{
            RedisPoolUtil.hdel(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS,remoteAddr+"_"+blogId.toString());
            result = iBlogService.cancelLike(blogId);
            message="取消点赞";
            code =ResponseCode.NOT_LIKE_SUCCESS.getCode();
        }
        if(result){
            return ServerResponse.createByErrorCodeAndMessage(code,message+"成功！");
        }else{
            return ServerResponse.createByErrorCodeAndMessage(code,message+"失败！");
        }
    }

    @RequestMapping("add_view.do")
    @ResponseBody
    public ServerResponse addLike(HttpServletRequest request,Integer blogId){
        if(blogId==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        boolean flag  = iBlogService.isExists(blogId);
        if(!flag){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //获取请求的ip
        String remoteAddr = request.getRemoteAddr();
        //查询redis中是否有该ip
        Boolean  remoteUser = cacheService.isBlogViewerExists(remoteAddr,blogId);
        boolean result = false;
        //如果没有点过赞 否则取消赞 然后记录到缓存中
        if(!remoteUser){
            //存入Redis
            RedisPoolUtil.sadd(Const.CacheTypeName.REMOTE_VIEW_USER+"_"+remoteAddr,blogId.toString(),Const.CacheTime.VIEW_COUNT_TIME);
            //修改数据库中的数据
            result =iBlogService.addViewCount(blogId);
        }
        if(result){
            return ServerResponse.createBySuccessMessage("您已成功贡献了一个浏览量！");
        }
        return ServerResponse.createByErrorMessage("贡献浏览量失败！");
    }

}

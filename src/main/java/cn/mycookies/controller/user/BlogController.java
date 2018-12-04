package cn.mycookies.controller.user;

import cn.mycookies.common.BlogStaticType;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import cn.mycookies.service.BlogService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
@Api("博客相关接口")
@ResponseBody
@RequestMapping("blogs")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    @ApiOperation(value ="分页查询列表博客",response = BlogVO.class,responseContainer = "List")
    public ServerResponse<PageInfo<BlogVO>> getBlogs(@ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize,
                                                     @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum,
                                                     @ApiParam(value = "分类Id") @RequestParam(required = false) Integer categoryId,
                                                     @ApiParam(value = "分类Id") @RequestParam(required = false) Integer tagId,
                                                     @ApiParam(value = "排序类型") @RequestParam(required = false,defaultValue = "create_time desc") String orderBy){

        return blogService.listBlogs(pageNum,pageSize,categoryId,tagId, DataStatus.NO_DELETED,orderBy);
    }

    @GetMapping("/{id}")
    @ApiOperation(value ="查找博客",response=BlogVO.class)
    public ServerResponse<BlogVO> getBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.getBlog(id,DataStatus.ALL,true,true);
    }

    @PutMapping("/count/{id}")
    @ApiOperation(value ="修改博客统计，如点赞，浏览量，评论数",response=Boolean.class)
    public ServerResponse getBlog(@ApiParam("博客id") @PathVariable Integer id,
                                  @ApiParam("操作类型，like，view，comment等")@RequestParam(defaultValue = BlogStaticType.LIKE) String type){
        return blogService.updateBlogCount(id,type);
    }

    @GetMapping("/index")
    @ApiOperation(value ="首页 内容初始化",response=Boolean.class)
    public ServerResponse<IndexVO> index(){

        return blogService.getIndexVO(true);
    }

    @GetMapping("/left")
    @ApiOperation(value ="侧边栏内容初始化",response=Boolean.class)
    public ServerResponse<IndexVO> left(){

        return blogService.getIndexVO(false);
    }

}

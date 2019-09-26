package cn.mycookies.controller.backend;

import cn.mycookies.common.base.BaseController;
import cn.mycookies.common.base.ServerResponse;
import cn.mycookies.pojo.dto.BlogAddRequest;
import cn.mycookies.pojo.dto.BlogListRequest;
import cn.mycookies.pojo.vo.BlogDetail4AdminVO;
import cn.mycookies.pojo.vo.BlogDetail4UserVO;
import cn.mycookies.service.BlogService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客管理端Controller
 *
 * @author Jann Lee
 * @date 2019-07-02 22:18
 */
@RequestMapping("manage/blogs")
@Api(description = "博客管理模块")
@RestController
public class ManageBlogController extends BaseController {

    @Autowired
    private BlogService blogService;

    @ApiOperation(value ="新增博客")
    @PostMapping
    public ServerResponse<Boolean> addBlog(@RequestBody BlogAddRequest blogAddRequest){
        validate(blogAddRequest);

        return blogService.createBlogInfo(blogAddRequest);
    }

    @GetMapping
    @ApiOperation(value ="分页查询列表博客",response = BlogDetail4UserVO.class,responseContainer = "List")
    public ServerResponse<PageInfo<BlogDetail4UserVO>> getBlogList(@ModelAttribute BlogListRequest blogListRequest){

        return blogService.getBlogListInfos(blogListRequest);
    }

    @PutMapping("/{id:\\d+}")
    @ApiOperation(value ="修改博客")
    public ServerResponse<Boolean> updateBlog(@ApiParam("博客id") @PathVariable Integer id,
                                              @RequestBody BlogAddRequest blogAddRequest){
        validate(blogAddRequest);

        return blogService.updateBlog(id, blogAddRequest);
    }

    @GetMapping("/{id:\\d+}")
    @ApiOperation(value ="查找博客",response= BlogDetail4UserVO.class)
    public ServerResponse<BlogDetail4AdminVO> getBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.getBlogDetailInfo(id);
    }

    @DeleteMapping("/{id:\\d+}")
    @ApiOperation(value ="删除博客")
    public ServerResponse<Boolean> deleteBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.deleteBlogInfo(id);
    }

}

package cn.mycookies.controller.backend;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.ProcessBindingResult;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogAddRequest;
import cn.mycookies.pojo.dto.BlogListQueryRequest;
import cn.mycookies.pojo.vo.BlogDetailVO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.service.BlogService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RequestMapping("manage/blogs")
@Api(description = "博客管理模块")
@ResponseBody
@Controller
public class ManageBlogController {

    @Autowired
    private BlogService blogService;

    @PostMapping
    @ApiOperation(value ="新增博客")
    public ServerResponse addBlog(@RequestBody @Valid BlogAddRequest blogAddRequest, BindingResult bindingResult){
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }

        return blogService.addBlog(blogAddRequest);
    }

    @GetMapping
    @ApiOperation(value ="分页查询列表博客",response = BlogVO.class,responseContainer = "List")
    public ServerResponse<PageInfo<BlogVO>> getBlogList(BlogListQueryRequest blogListQueryRequest){

        return blogService.getBlogListInfos(blogListQueryRequest);
    }

    @PutMapping("/{id}")
    @ApiOperation(value ="修改博客")
    public ServerResponse updateBlog(@RequestBody @Valid BlogAddRequest blogAddRequest, BindingResult bindingResult,
                                     @ApiParam("博客id") @PathVariable Integer id){
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }
        blogAddRequest.setId(id);

        return blogService.updateBlog(blogAddRequest);
    }

    @GetMapping("/{id}")
    @ApiOperation(value ="查找博客",response=BlogVO.class)
    public ServerResponse<BlogDetailVO> getBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.getBlogDetailVOById(id);
    }



}

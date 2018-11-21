package cn.mycookies.controller.backend;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ProcessBindingResult;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.dto.TagAdd;
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
    public ServerResponse addBlog(@RequestBody @Valid BlogDTO blogAdd, BindingResult bindingResult){
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }


        return blogService.addBlog(blogAdd);
    }

    @GetMapping
    @ApiOperation(value ="分页查询列表博客",response = BlogVO.class,responseContainer = "List")
    public ServerResponse<PageInfo<BlogVO>> getBlogs(@ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize,
                                             @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum,
    @ApiParam(value = "分类Id") @RequestParam(required = false) Byte categoryId,
    @ApiParam(value = "分类Id") @RequestParam(required = false) Byte tagId,
    @ApiParam(value = "是否被删除") @RequestParam(required = false) @Min(0)@Max(2)Integer idDeleted,
    @ApiParam(value = "排序类型") @RequestParam(required = false,defaultValue = "create_time desc") String orderBy){

        return blogService.getBlogs(pageNum,pageSize,categoryId,tagId,idDeleted,orderBy);
    }

    @PostMapping("/{id}")
    @ApiOperation(value ="修改博客")
    public ServerResponse updateBlog(@RequestBody @Valid BlogDTO blogAdd, BindingResult bindingResult,
                                     @ApiParam("博客id") @PathVariable Integer id){
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }
        blogAdd.setId(id);

        return blogService.updateBlog(blogAdd);
    }

    @GetMapping("/{id}")
    @ApiOperation(value ="查找博客",response=BlogVO.class)
    public ServerResponse<BlogVO> getBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.getBlog(id,DataStatus.ALL);
    }



}

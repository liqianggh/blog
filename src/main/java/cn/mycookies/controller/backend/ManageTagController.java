package cn.mycookies.controller.backend;

import cn.mycookies.common.BaseController;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.TagAddRequest;
import cn.mycookies.pojo.dto.TagListRequest;
import cn.mycookies.pojo.dto.TagUpdateRequest;
import cn.mycookies.pojo.dto.TagVO;
import cn.mycookies.service.TagService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 标签管理controller
 *
 * @author Jann Lee
 * @date 2018-11-18 16:36
 **/
@Controller
@Api(description = "标签管理")
@RequestMapping("manage/tags")
@ResponseBody
public class ManageTagController extends BaseController{

    @Autowired
    private TagService tagService;

    @GetMapping
    @ApiOperation(value = "获取标签列表", response = TagVO.class, responseContainer = "List")
    public ServerResponse<PageInfo<TagVO>> tagList(@ModelAttribute TagListRequest tagListRequest) {

        return tagService.getTagListInfos(tagListRequest);
    }

    @PostMapping
    @ApiOperation(value = "添加标签", response = Boolean.class)
    public ServerResponse<Boolean> addTag(@ModelAttribute @RequestBody TagAddRequest tagAddRequest) {
        validate(tagAddRequest);
        
        return tagService.createTagInfo(tagAddRequest);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改标签", response = Boolean.class)
    public ServerResponse updateTag(@ModelAttribute @RequestBody TagUpdateRequest tagUpdateRequest, @PathVariable(name = "id") Integer id) {
        validate(tagUpdateRequest);        
        
        return tagService.updateTagInfo(id, tagUpdateRequest);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查找标签", response = Boolean.class)
    public ServerResponse<TagVO> selectTag(@PathVariable(name = "id") Integer id) {

        return tagService.getTagDetailInfoById(id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除标签", response = Boolean.class)
    public ServerResponse<TagVO> deleteTag(@PathVariable(name = "id") Integer id) {

        return tagService.deleteTagInfoById(id);
    }

}

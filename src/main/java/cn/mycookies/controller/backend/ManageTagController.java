package cn.mycookies.controller.backend;

import cn.mycookies.common.*;
import cn.mycookies.pojo.dto.TagAdd;
import cn.mycookies.pojo.dto.TagBo;
import cn.mycookies.pojo.po.Tag;
import cn.mycookies.service.TagService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @ClassName TagController
 * @Description 标签管理
 * @Author Jann Lee
 * @Date 2018-11-18 16:36
 **/
@Controller
@Api(description = "标签管理")
@RequestMapping("manage/tags")
@ResponseBody
public class ManageTagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    @ApiOperation(value = "获取标签列表", response = TagBo.class, responseContainer = "List")
    public PageInfo<TagBo> tagList(
            @ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum,
             @ApiParam(value = "标签类型，是分类还是标签") @RequestParam(defaultValue = TagTypes.TAG_LABEL+"") Byte type) {

        return tagService.findTagList(pageNum, pageSize,type);
    }

    @PostMapping("")
    @ApiOperation(value = "添加标签", response = Boolean.class)
    public ServerResponse<Boolean> addTag(@RequestBody @Valid TagAdd tagAdd, BindingResult bindingResult) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }
        return tagService.addTag(tagAdd);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "修改标签", response = Boolean.class)
    public ServerResponse updateTag(@RequestBody @Valid Tag tag,
                                    BindingResult bindingResult,
                                    @RequestParam(name = "type",required = true)Byte type,
                                    @PathVariable(name = "id", required = true) Integer id) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }
        tag.setId(id);
        tag.setType(type);
        return tagService.updateTag(tag);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查找标签", response = Boolean.class)
    public ServerResponse<TagBo> selectTag(
            @PathVariable(name = "id", required = true) Integer id,
            @RequestParam(name = "type", required =true) Byte type) {

        return tagService.selectTagById(id,type);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除标签", response = Boolean.class)
    public ServerResponse<TagBo> deleteTag(@PathVariable(name = "id", required = true) Integer id,
                                           @RequestParam(name = "type", required = true) Byte type) {

        return tagService.deleteById(id,type,DataStatus.DELETED);
    }

}

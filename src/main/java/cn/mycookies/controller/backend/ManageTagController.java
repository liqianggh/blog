package cn.mycookies.controller.backend;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.ProcessBindingResult;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.bo.TagAdd;
import cn.mycookies.pojo.bo.TagBo;
import cn.mycookies.pojo.po.Tag;
import cn.mycookies.service.TagService;
import cn.mycookies.utils.JsonUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Objects;

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

    @GetMapping("")
    @ApiOperation(value = "获取标签列表", response = TagBo.class, responseContainer = "List")
    public PageInfo<TagBo> tagList(@ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize, @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum) {

        return tagService.findTagList(pageNum, pageSize);
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
    public ServerResponse updateTag(@RequestBody @Valid Tag tag, BindingResult bindingResult, @PathVariable(name = "id", required = true) Integer id) {

        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }
        tag.setId(id);
        return tagService.updateTag(tag);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "查找标签", response = Boolean.class)
    public ServerResponse<TagBo> selectTag(@PathVariable(name = "id", required = true) Integer id) {

        return tagService.selectTagById(id);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "查找标签", response = Boolean.class)
    public ServerResponse<TagBo> deleteTag(@PathVariable(name = "id", required = true) Integer id) {

        return tagService.deleteById(id);
    }

}

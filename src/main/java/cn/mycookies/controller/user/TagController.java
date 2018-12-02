package cn.mycookies.controller.user;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagTypes;
import cn.mycookies.pojo.dto.TagDTO;
import cn.mycookies.pojo.vo.TagVO;
import cn.mycookies.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className TagController
 * @description TODO
 * @author Jann Lee
 * @date 2018-11-21 0:03
 **/
@Controller
@Api("标签/分类相关接口")
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/{type}")
    @ApiOperation(value = "获取标签列表", response = TagDTO.class, responseContainer = "List")
    @ResponseBody
    public ServerResponse<List<TagVO>> tagList(@ApiParam(value = "标签类型，是分类还是标签") @PathVariable(required = true) Byte type) {
        if (type !=1 && type != 2){
            type = 2;
        }
        return tagService.listTagVOs(type);
    }
}

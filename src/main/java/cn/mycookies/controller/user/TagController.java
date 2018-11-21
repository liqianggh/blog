package cn.mycookies.controller.user;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagTypes;
import cn.mycookies.pojo.dto.TagBo;
import cn.mycookies.pojo.vo.TagVO;
import cn.mycookies.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName TagController
 * @Description TODO
 * @Author Jann Lee
 * @Date 2018-11-21 0:03
 **/
@Controller
@Api("标签/分类相关接口")
@RequestMapping("tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    @ApiOperation(value = "获取标签列表", response = TagBo.class, responseContainer = "List")
    @ResponseBody
    public ServerResponse<List<TagVO>> tagList(@ApiParam(value = "标签类型，是分类还是标签") @RequestParam(defaultValue = TagTypes.TAG_LABEL+"") Byte type) {

        return tagService.findTagVoList(type);
    }
}

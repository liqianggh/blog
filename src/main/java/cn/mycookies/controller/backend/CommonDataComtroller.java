package cn.mycookies.controller.backend;

import cn.mycookies.common.BaseController;
import cn.mycookies.common.KeyValueVO;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagTypes;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 公共数据接口
 * @author Jann Lee
 * @description TODO
 * @date 2019-04-19 22:54
 **/
@Api("公共数据接口")
@RestController
@RequestMapping("/common")
public class CommonDataComtroller extends BaseController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有博客分类
     * @return
     */
    @GetMapping("/all/categories")
    @ApiOperation(value ="查找博客",response= BlogVO.class)
    public ServerResponse<List<KeyValueVO<Integer, String>>> getAllCategoryInfos(){
        return ServerResponse.createBySuccess(tagService.getAllTagList(TagTypes.TAG_CATEGORY));
    }

    /**
     * 获取所有博客标签
     */
    @GetMapping("/all/tags")
    public ServerResponse<List<KeyValueVO<Integer,String>>> getAllTagInfos(){
        return ServerResponse.createBySuccess(tagService.getAllTagList(TagTypes.TAG_LABEL));
    }
}

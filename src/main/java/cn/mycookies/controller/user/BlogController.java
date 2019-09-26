package cn.mycookies.controller.user;

import cn.mycookies.common.base.BaseController;
import cn.mycookies.common.constants.BlogStaticType;
import cn.mycookies.common.base.ServerResponse;
import cn.mycookies.pojo.dto.BlogListRequest;
import cn.mycookies.pojo.vo.BlogDetail4UserVO;
import cn.mycookies.pojo.vo.IndexVO;
import cn.mycookies.service.BlogService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客相关接口
 *
 * @author Jann Lee
 * @date 2019-09-27 0:22
 */
@Api("博客相关接口")
@RequestMapping("blogs")
@CrossOrigin
@RestController
public class BlogController extends BaseController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    @ApiOperation(value ="分页查询列表博客",response = BlogDetail4UserVO.class,responseContainer = "List")
    public ServerResponse<PageInfo<BlogDetail4UserVO>> getBlogListInfos(@ModelAttribute BlogListRequest queryRequest){

        return blogService.getBlogListInfos(queryRequest);
    }

    @GetMapping("/{id:\\d+}")
    @ApiOperation(value ="获取博客详情",response= BlogDetail4UserVO.class)
    public ServerResponse<BlogDetail4UserVO> getBlog(@ApiParam("博客id") @PathVariable Integer id){

        return blogService.getBlogDetailsInfo(id);
    }

    @PutMapping("/count/{id:\\d+}")
    @ApiOperation(value ="修改博客统计，如点赞，浏览量，评论数",response=Boolean.class)
    public ServerResponse getBlog(@ApiParam("博客id") @PathVariable Integer id,
                                  @ApiParam("操作类型，addLikeCount，view，comment等")@RequestParam(defaultValue = BlogStaticType.LIKE) String type){
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

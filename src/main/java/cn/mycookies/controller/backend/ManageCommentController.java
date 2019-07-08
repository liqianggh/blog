package cn.mycookies.controller.backend;

import cn.mycookies.common.BaseController;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.CommentListRequest;
import cn.mycookies.pojo.vo.CommentListItemVO;
import cn.mycookies.service.CommentService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评论管理
 *
 * @author Jann Lee
 * @date 2019-07-04 23:08
 */
@RequestMapping("manage/comments")
@Api(description = "用户评论模块")
@RestController
public class ManageCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @DeleteMapping("/{id:\\d+}")
    @ApiOperation(value = "删除评论,如果有回复不能删除")
    public ServerResponse deleteCommentInfos(@ApiParam(value = "评论的id") @PathVariable Integer id){

        return commentService.deleteCommentInfo(id);
    }

    @GetMapping
    @ApiOperation(value = "获取评论列表",responseContainer = "PageInfo",response = CommentListItemVO.class)
    public ServerResponse<PageInfo<CommentListItemVO>> getCommentInfos(@ModelAttribute CommentListRequest commentListRequest){

        return commentService.getCommentInfos(null, commentListRequest);
    }
}

package cn.mycookies.controller.backend;

import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.vo.CommentVO;
import cn.mycookies.service.CommentService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RequestMapping("manage/comments")
@Api(description = "用户评论模块")
@ResponseBody
@Controller
public class ManageCommentController {

    @Autowired
    private CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ApiOperation(value = "删除评论和它相关的回复")
    public ServerResponse deleteComments(@ApiParam(value = "评论的id") @RequestParam(required = true) Integer commentId){

        return commentService.deleteComment(commentId, DataStatus.DELETED);
    }

    @GetMapping("/{targetId}")
    @ApiOperation(value = "获取评论列表",responseContainer = "PageInfo",response = CommentVO.class)
    public ServerResponse<PageInfo<CommentVO>> comments(@ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize,
                                                        @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum,
                                                        @ApiParam(value = "评论的状态") @RequestParam(defaultValue = DataStatus.ALL+"")@Min(0)@Max(2) Byte is_deleted,
                                                        @ApiParam(value = "评论主体的id") @PathVariable Integer targetId,
                                                        @ApiParam(value = "对话的id") @RequestParam(required = false) Integer sessionId){

        return commentService.getComments(pageNum,pageSize,null,null,targetId,sessionId,is_deleted);
    }
}

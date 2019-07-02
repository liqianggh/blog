package cn.mycookies.controller.user;

import cn.mycookies.common.*;
import cn.mycookies.pojo.dto.CommentDTO;
import cn.mycookies.pojo.vo.CommentVO;
import cn.mycookies.service.CommentService;
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
 * @className TagController
 * @description 评论管理
 * @author Jann Lee
 * @date 2018-11-20 12:20
 **/
@Controller
@Api(description = "用户评论模块")
@ResponseBody
@RequestMapping("comments")
@CrossOrigin
public class CommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    @ApiOperation(value = "添加评论")
    public ServerResponse addComment(@RequestBody @Valid CommentDTO commentDTO, BindingResult bindingResult) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ProcessBindingResult.process(bindingResult));
        }

        return commentService.insertComment(commentDTO);
    }

    @GetMapping("/{targetId}")
    @ApiOperation(value = "获取评论列表", responseContainer = "PageInfo", response = CommentVO.class)
    public ServerResponse<PageInfo<CommentVO>> comments(
            @ApiParam(value = "当前页数") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam(value = "每页展示条数") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "评论主体的id") @PathVariable Integer targetId,
            @ApiParam(value = "对话的id") @RequestParam(required = false) String sessionId) {

        return commentService.listComments(pageNum, pageSize, null, null, targetId, sessionId, DataStatus.NO_DELETED);
    }
    @PutMapping("/like/{id}")
    @ApiOperation(value = "获取评论列表", responseContainer = "PageInfo", response = CommentVO.class)
    public ServerResponse<String> like(
           @ApiParam(value ="评论的id") @PathVariable(value = "id",required = true) Integer id){

        return commentService.updateCommentLikeCount(id);
    }


    @DeleteMapping("/{commentId}")
    @ApiOperation(value = "删除评论和它相关的回复")
    public ServerResponse deleteComments(@ApiParam(value = "评论的id") @RequestParam(required = true) Integer commentId) {

        return commentService.deleteComment(commentId, DataStatus.NO_DELETED);
    }

}

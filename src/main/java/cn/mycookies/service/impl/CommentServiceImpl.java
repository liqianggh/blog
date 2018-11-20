package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.CommentMapper;
import cn.mycookies.pojo.po.Comment;
import cn.mycookies.pojo.vo.CommentVO;
import cn.mycookies.service.CommentService;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.jdbc.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public ServerResponse addComment(Comment comment) {
        if (comment == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        } else {
            if (Objects.isNull(comment.getTargetId()) || Objects.isNull(comment.getUserId()) || StringUtils.isEmptyOrWhitespaceOnly(comment.getContent())) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
            }
            // 如果是回复消息 但是没有sessionId返回错误
            if (comment.getReplyUid() != null && comment.getSessionId() == null) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());

            }
        }

        int result = commentMapper.insertSelective(comment);
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }

    @Override
    public ServerResponse<PageInfo<CommentVO>> getComments(Integer pageNum, Integer pageSize, Integer userId, Integer replyUid, Integer targetId,Integer sessionId, Integer isDeleted) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("like_count desc, create_time desc");
        if (isDeleted == DataStatus.ALL) {
            isDeleted = null;
        }
        if (targetId == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setStatus(isDeleted);
        comment.setReplyUid(replyUid);
        comment.setTargetId(targetId);
        comment.setSessionId(sessionId);
        List<CommentVO> commentList = commentMapper.selectComments(comment);

        if (commentList == null || commentList.size() == 0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
        // 日期转换
         commentList.stream().forEach(commentTemp -> {
             commentTemp.setCreateTimeStr(DateTimeUtil.dateToStr(comment.getCreateTime()));
        });
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(commentList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse deleteComment(Integer commentId, Integer isRealDelete) {
        int result = 0;
        // 判断是否存在
        Comment commentResult = commentMapper.selectCommentById(commentId);
        if (commentResult == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        // 判断是真删除还是逻辑删除
        if (isRealDelete == DataStatus.DELETED) {
            result = commentMapper.deleteComment(commentId);
        } else {
            Comment comment = new Comment();
            comment.setId(commentId);
            comment.setStatus(isRealDelete);
            result = commentMapper.updateComment(comment);
        }
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }

}

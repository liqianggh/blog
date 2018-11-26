package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.CommentMapper;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.vo.CommentVO;
import cn.mycookies.service.CommentService;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public ServerResponse insertComment(CommentDO commentDO, String username) {

        String email = commentDO.getEmail();
        // 用户是否注册过，注册过就不用username


        if (commentDO == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        } else {
            if (Objects.isNull(commentDO.getTargetId()) || StringUtils.isNullOrEmpty(commentDO.getEmail()) || StringUtils.isEmptyOrWhitespaceOnly(commentDO.getContent())) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
            }
            // 如果是回复消息 但是没有sessionId返回错误
            if (StringUtils.isNullOrEmpty(commentDO.getReplyEmail()) && commentDO.getSessionId() == null) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());

            }
        }

        int result = commentMapper.insertSelective(commentDO);
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }

    @Override
    public ServerResponse<PageInfo<CommentVO>> listComments(Integer pageNum, Integer pageSize, String email, String repluEmail, Integer targetId, Integer sessionId, Byte isDeleted) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("t.like_count desc, t.create_time desc");
        if (isDeleted == DataStatus.ALL) {
            isDeleted = null;
        }
        if (targetId == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        }
        CommentDO commentDO = new CommentDO();
        commentDO.setEmail(email);
        commentDO.setIsDeleted(isDeleted);
        commentDO.setReplyEmail(repluEmail);
        commentDO.setTargetId(targetId);
        commentDO.setSessionId(sessionId);
        List<CommentVO> commentList = commentMapper.selectComments(commentDO);

        if (commentList == null || commentList.size() == 0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        // 日期转换
         commentList.stream().forEach(commentTemp -> {
             commentTemp.setCreateTimeStr(DateTimeUtil.dateToStr(commentDO.getCreateTime()));
        });
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(commentList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse deleteComment(Integer commentId, Byte isRealDelete) {
        int result = 0;
        // 判断是否存在
        CommentDO commentDOResult = commentMapper.selectCommentById(commentId);
        if (commentDOResult == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        // 判断是真删除还是逻辑删除
        if (isRealDelete == DataStatus.DELETED) {
            result = commentMapper.deleteComment(commentId);
        } else {
            CommentDO commentDO = new CommentDO();
            commentDO.setId(commentId);
            commentDO.setIsDeleted(isRealDelete);
            result = commentMapper.updateComment(commentDO);
        }
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }

    @Override
    public ServerResponse<String> updateCommentLikeCount(Integer id) {

        // 判断是否存在
        CommentDO commentDO = commentMapper.selectCommentById(id);
        if (commentDO == null){
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(),ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        int result = commentMapper.likeComment(id);
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorMessage("点赞失败");
        }
     }

}

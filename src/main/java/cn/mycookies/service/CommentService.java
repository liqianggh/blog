package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.vo.CommentVO;
import com.github.pagehelper.PageInfo;


public interface CommentService {

    ServerResponse insertComment(CommentDO commentDO);

    ServerResponse<PageInfo<CommentVO>> listComments(Integer pageNum, Integer pageSize, Integer userId, Integer replyUid, Integer targetId, Integer sessionId, Byte isDeleted);

    ServerResponse deleteComment(Integer commentId,Byte isRealDelete);

    ServerResponse<String> updateCommentLikeCount(Integer id);
}

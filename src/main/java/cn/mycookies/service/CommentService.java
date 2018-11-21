package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.po.Comment;
import cn.mycookies.pojo.vo.CommentVO;
import com.github.pagehelper.PageInfo;

public interface CommentService {
    ServerResponse addComment(Comment comment);

    ServerResponse<PageInfo<CommentVO>> getComments(Integer pageNum, Integer pageSize,Integer userId,Integer replyUid, Integer targetId,Integer sessionId,Byte isDeleted);

    ServerResponse deleteComment(Integer commentId,Byte isRealDelete);

    ServerResponse<String> likeComment(Integer id);
}

package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.vo.CommentVO;
import com.github.pagehelper.PageInfo;


public interface CommentService {

<<<<<<< HEAD
    ServerResponse insertComment(CommentDO commentDO,String username);

    ServerResponse<PageInfo<CommentVO>> listComments(Integer pageNum, Integer pageSize, String email, String replyEmail, Integer targetId, Integer sessionId, Byte isDeleted);
=======
    ServerResponse insertComment(CommentDO commentDO);

    ServerResponse<PageInfo<CommentVO>> listComments(Integer pageNum, Integer pageSize, Integer userId, Integer replyUid, Integer targetId, Integer sessionId, Byte isDeleted);
>>>>>>> 5b7234e66165bd7a8c06bbf0c43bdc6e3c0624e2

    ServerResponse deleteComment(Integer commentId,Byte isRealDelete);

    ServerResponse<String> updateCommentLikeCount(Integer id);
}

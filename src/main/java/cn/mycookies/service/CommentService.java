package cn.mycookies.service;

import cn.mycookies.common.BaseService;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.YesOrNoType;
import cn.mycookies.common.exception.BusinessException;
import cn.mycookies.dao.CommentMapper;
import cn.mycookies.pojo.dto.CommentAddRequest;
import cn.mycookies.pojo.dto.CommentListRequest;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.po.CommentExample;
import cn.mycookies.pojo.vo.CommentListItemVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

/**
 * 评论相关service
 *
 * @author Jann Lee
 * @date 2019-07-04 23:12
 */
@Service
@NotThreadSafe
public class CommentService extends BaseService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 添加评论
     *
     * @param commentAddRequest
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public ServerResponse<Boolean> addCommentInfo(CommentAddRequest commentAddRequest) {
        CommentDO commentDO = new CommentDO();
        ServerResponse<Boolean> validateResult = validateAndInitAddRequest(commentAddRequest, commentDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        // todo 处理用户信息

        if (commentMapper.insert(commentDO) == 0) {
            return resultError4DB("评论失败");
        }
        return resultOk();
    }

    /**
     * 校验添加参数
     *
     * @param commentAddRequest
     * @param commentDO
     * @return
     */
    private ServerResponse<Boolean> validateAndInitAddRequest(CommentAddRequest commentAddRequest, CommentDO commentDO) {
//        if (commentAddRequest == null || StringUtils.isEmpty(commentAddRequest.getContent()) || StringUtils.isEmpty(commentAddRequest.getUserEmail()) || commentAddRequest.get() == null) {
//            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
//        }
        return null;
    }

    /**
     * 获取评论列表
     * 1.如果是博客评论
     *
     * @param targetId
     * @param commentListRequest
     * @return
     */
    public ServerResponse<PageInfo<CommentListItemVO>> getCommentInfos(Integer targetId, CommentListRequest commentListRequest) {
        Page page = getPage(commentListRequest);
        Preconditions.checkNotNull(commentListRequest, "评论请求参数不能为null");
        Preconditions.checkNotNull(commentListRequest.getTargetType(), "主体类型不能为null");
        Preconditions.checkNotNull(targetId, "主体id不能为null");
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria()
                .andTargetIdEqualTo(targetId)
                .andTargetTypeEqualTo(commentListRequest.getTargetType().getCode())
                .andCommentStatusEqualTo(YesOrNoType.YES.getCode());
        commentExample.setOrderByClause(page.getOrderBy());

        List<CommentDO> commentList = commentMapper.selectByExample(commentExample);
//        // 日期转换
//        commentList.stream().forEach(commentTemp -> {
//            commentTemp.setCreateTimeStr(DateTimeUtil.dateToStr(commentDO.getCreateTime()));
//        });

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(commentList);

        return resultOk(pageInfo);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @return
     */
    public ServerResponse<Boolean> deleteCommentAndChildren(Integer commentId) {
        Preconditions.checkNotNull(commentId, "要删除的评论id不能为null");

        return resultOk();
    }

    /**
     * todo 缓存
     * 给评论点赞
     *
     * @param commentId
     * @return
     */
    public ServerResponse<String> addCommentLikeCount(Integer commentId) {
        CommentDO commentDO = commentMapper.selectByPrimaryKey(commentId);
        checkExists(commentDO);
        fillUpdateTime(commentDO);
        commentDO.setLikeCount(commentDO.getLikeCount() + 1);
        if (commentMapper.updateByPrimaryKeySelective(commentDO) == 0) {
            return ServerResponse.createByErrorMessage("点赞失败");
        }
        return resultOk();
    }

}

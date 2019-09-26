package cn.mycookies.service;

import cn.mycookies.common.base.BaseService;
import cn.mycookies.common.constants.CommentTargetType;
import cn.mycookies.common.base.ServerResponse;
import cn.mycookies.common.constants.YesOrNoType;
import cn.mycookies.dao.CommentMapper;
import cn.mycookies.pojo.dto.CommentAddRequest;
import cn.mycookies.pojo.dto.CommentListRequest;
import cn.mycookies.pojo.meta.CommentDO;
import cn.mycookies.pojo.meta.CommentExample;
import cn.mycookies.pojo.meta.UserDO;
import cn.mycookies.pojo.vo.CommentListItemVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    @Resource
    private UserService userService;

    @Resource
    private BlogService blogService;


    /**
     * 添加评论
     *
     * @param commentAddRequest
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public ServerResponse<Boolean> addCommentInfo(CommentAddRequest commentAddRequest) {
        if (!isValidComment()){
            return resultError("评论过于频繁，请稍后再试");
        }
        CommentDO commentDO = new CommentDO();
        ServerResponse<Boolean> validateResult = validateAndInitAddRequest(commentAddRequest, commentDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        if (commentMapper.insertSelective(commentDO) == 0) {
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
        String email = commentAddRequest.getUserEmail();
        Integer targetId = commentAddRequest.getTargetId();
        CommentTargetType targetType = commentAddRequest.getTargetType();
        String content = commentAddRequest.getContent();

        Preconditions.checkNotNull(commentDO, "内部参数错误");
        Preconditions.checkArgument(StringUtils.isNotEmpty(email), "邮箱不能为空");
        Preconditions.checkNotNull(targetId, "被评论的主体不能为null");
        Preconditions.checkNotNull(commentAddRequest, "评论信息不能为null");
        Preconditions.checkArgument(StringUtils.isNotEmpty(content), "评论内容不能为空");
        commentDO.setContent(commentAddRequest.getContent());

        Integer pid = commentAddRequest.getPid();
        // 1.pid不为null表明是回复信息，需要判断被回复的评论是否存在
        if (Objects.nonNull(pid)) {
            if (Objects.isNull(commentMapper.selectByPrimaryKey(pid))) {
                return resultError4Param("被回复的主体不存在");
            }
        }
        // 2. 如果是博客留言
        switch (targetType) {
            case ARTICLE:
                // 判断博客是否存在
                if (Objects.isNull(blogService.getBlogById(targetId))) {
                    return resultError4Param("被评论的主体不存在");
                }
                break;
            //3. 如果是留言板, 被评论的是一个留言，如果targetId不存在则等于pid
            case MESSAGE_BOARD:

                break;
            case COMMENT_REPLY:
                if (Objects.isNull(pid)) {
                    return resultError4Param("父级评论不存在");
                }
                if (Objects.isNull(commentMapper.selectByPrimaryKey(targetId))) {
                    return resultError4Param("被回复的主体不存在");
                }
                break;
            default:
                return resultError4Param("被评论的主体不明确");
        }

        // 4. 校验用户信息，并更新或添加用户数据
        UserDO user;
        ServerResponse<Boolean> updateResult = resultOk();
        if (Objects.isNull(user = userService.getUserDOByEmail(email))) {
            String userName = commentAddRequest.getUserName();
            if (StringUtils.isEmpty(userName)) {
                return resultError4Param("首次评论必须填写用户名");
            }
            user = new UserDO();
            user.setUserEmail(email);
            user.setUserName(userName);
            user.setUserStatus(YesOrNoType.YES.getCode());
            // 添加用户信息
            updateResult = userService.createUserInfo(user);
            // 判断用户名是否存在
        } else {
            if (Objects.nonNull(commentAddRequest.getUserName()) && !Objects.equals(user.getUserName(), commentAddRequest.getUserName())) {
                user.setUserName(commentAddRequest.getUserName());
                fillUpdateTime(user);
                updateResult = userService.updateVisitorInfo(user);
            }
        }
        if (!updateResult.isOk()) {
            return updateResult;
        }

        BeanUtils.copyProperties(commentAddRequest, commentDO);
        commentDO.setUserName(user.getUserName());
        commentDO.setTargetType(targetType.getCode());
        fillCreateTime(commentDO);
        return resultOk();
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
        Preconditions.checkNotNull(commentListRequest, "评论请求参数不能为null");
        Integer pid = commentListRequest.getPid();
        CommentTargetType targetType = commentListRequest.getTargetType();
        Preconditions.checkNotNull(targetType, "主体类型不能为null");
        // 如果时留言板消息的话，当pid为null时，targetId可以为null
        Page page = getPage(commentListRequest);
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andTargetTypeEqualTo(targetType.getCode())
                .andCommentStatusEqualTo(YesOrNoType.YES.getCode());
        commentExample.setOrderByClause(page.getOrderBy());
        List<CommentDO> commentList;
        if (Objects.nonNull(pid)) {
            criteria.andTargetIdEqualTo(targetId);
         } else if (Objects.nonNull(targetId)) {
            criteria.andTargetIdEqualTo(targetId);
         } else if (!Objects.equals(targetType, CommentTargetType.MESSAGE_BOARD)){
            return resultError4Param("参数不合法");
         }
        commentList  = commentMapper.selectByExample(commentExample);
        List<CommentListItemVO>  resultList =  commentList.stream().map(CommentListItemVO::createFrom).collect(Collectors.toList());
        /**
         * 查询每个留言的评论
         */
        resultList.forEach(item ->{
            item.setChildrenComments(getCommentReplyById(item.getId()).stream().map(CommentListItemVO::createFrom).collect(Collectors.toList()));
        });
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(resultList);

        return resultOk(pageInfo);
    }

    public List<CommentDO> getCommentReplyById(int id) {
        CommentExample commentExample = new CommentExample();
        CommentExample.Criteria criteria = commentExample.createCriteria();
        criteria.andTargetTypeEqualTo(CommentTargetType.COMMENT_REPLY.getCode())
                .andCommentStatusEqualTo(YesOrNoType.YES.getCode());
        commentExample.setOrderByClause("create_time");

        return commentMapper.selectByExample(commentExample);
    }


    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @return
     */
    public ServerResponse<Boolean> deleteCommentInfo(Integer commentId) {
        Preconditions.checkNotNull(commentId, "要删除的评论id不能为null");
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andPidEqualTo(commentId);
        if (CollectionUtils.isNotEmpty(commentMapper.selectByExample(commentExample))){
            return resultError4Param("当前评论有回复哦");
        }
        if (commentMapper.deleteByPrimaryKey(commentId) == 0) {
            return resultError4DB("删除失败");
        }
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
        if (!isValidViewOrLike(String.valueOf(commentId))) {
            return resultError4DB("您已经点过赞了");
        }
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

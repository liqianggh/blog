package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.CommentType;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.CommentDOMapper;
import cn.mycookies.pojo.dto.CommentDTO;
import cn.mycookies.pojo.dto.UserDTO;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.pojo.vo.CommentVO;
import cn.mycookies.service.CommentService;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDOMapper commentMapper;

    @Override
    @Transactional
    public ServerResponse insertComment(CommentDTO commentDTO) {
        String username = commentDTO.getUsername();
        // 无论是评论 还是回复 ，email和content都不能为空
        if (commentDTO == null || StringUtils.isEmpty(commentDTO.getContent())
                || StringUtils.isEmpty(commentDTO.getEmail())
                || commentDTO.getTargetId()==null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        }
        String email = commentDTO.getEmail();
        // 用户是否注册过，注册过就不用username
        UserDTO result = commentMapper.getUserByEmail(email);
        // 如果为空，并且username不为空，插入数据
        if (Objects.nonNull(result)) {
            // 注册后再次输入的话视为修改username
            // todo 修改名字
        } else {
            if (StringUtils.isEmpty(username)) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), "请输入的用户名");
            }
            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setUserName(username);
            int resultCount = commentMapper.insertUser(userDTO);
            if (resultCount == 0) {
                return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(),"新增用户失败");
            }
        }

        // 如果是回复消息 但是没有sessionId返回错误
        int insertResult = 0;
        if (commentDTO.getTargetId() == CommentType.MESSAGE_REPLY || commentDTO.getTargetId() == CommentType.MESSAGE_BOARD) {
            if (StringUtils.isNotEmpty(commentDTO.getReplyEmail()) && StringUtils.isEmpty(commentDTO.getSessionId())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
            } else {
                // 会话id和回复reply email都在 说明是回复 直接插入
                // todo sessionId以及targeId的合法性
                insertResult = commentMapper.insertSelective(null);
            }
        } else {
            // 如果是留言或者
            UUID uuid = UUID.randomUUID();
            commentDTO.setSessionId(uuid.toString());
            insertResult = commentMapper.insertSelective(null);
        }
        if (insertResult > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }

    @Override
    public ServerResponse<PageInfo<CommentVO>> listComments(Integer pageNum, Integer pageSize, String email, String repluEmail, Integer targetId, String sessionId, Byte isDeleted) {
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
        commentDO.setCommentStatus(Integer.valueOf(isDeleted));
        commentDO.setReplyEmail(repluEmail);
        commentDO.setTargetId(targetId);
        if (StringUtils.isNotBlank(sessionId)) {
            commentDO.setSessionId(sessionId);
        }
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
            commentDO.setCommentStatus(Integer.valueOf(isRealDelete));
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

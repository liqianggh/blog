package cn.mycookies.pojo.vo;

import cn.mycookies.common.CommentTargetType;
import cn.mycookies.pojo.po.CommentDO;
import cn.mycookies.utils.DateTimeUtil;
import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 评论vo
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论回复实体类")
public class CommentListItemVO {

    private Integer id;

    /**
     * 被回复的id
     */
    private Integer pid;
    /**
     * 被评论目标的id
     */
    private Integer targetId;
    /**
     * 被评论目标的类型、评论/博客/留言版
     */
    private CommentTargetType targetType;
    /**
     * 用户邮箱
     */
    private String userEmail;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userIcon;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 评论下面的回复、回复之间的对话
     */
    private List<CommentListItemVO> childrenComments;

    /**
     * 构建vo根据do
     * @param commentDO
     * @return
     */
    public static CommentListItemVO createFrom(CommentDO commentDO){
        Preconditions.checkNotNull(commentDO, "构建vo的参数不能为null");
        CommentListItemVO targetVO = new CommentListItemVO();
        targetVO.setCreateTime(DateTimeUtil.dateToStr(commentDO.getCreateTime()));
        BeanUtils.copyProperties(commentDO, targetVO);
        return targetVO;
    }
}

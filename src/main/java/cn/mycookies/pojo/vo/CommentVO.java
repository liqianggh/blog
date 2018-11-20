package cn.mycookies.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论回复实体类")
public class CommentVO {

    private Integer id;

    private Integer sessionId;
    /**
     * 被评论的主体id
     */
    private Integer targetId;

    private Integer userId;
    private String userName;

    private Integer replyUid;
    private String replyUserName;
    /**
     * 正文
     */
    private String content;
    /**
     * 点赞数量
     */
    private Integer likeCount;

    /**
     * 时间
     */
    private String createTimeStr;

    private Date createTime;

}

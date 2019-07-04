package cn.mycookies.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论do
 *
 * @author Jann Lee
 * @date 2019-07-04 22:56
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDO extends BaseDO {

    private Integer id;

    private Integer pid;

    private Integer targetId;

    private Integer targetType;

    private String userEmail;

    private String userName;

    private String userIcon;

    private String content;

    private Integer likeCount;

    private Long createTime;

    private Long updateTime;

    private Integer commentStatus;
}
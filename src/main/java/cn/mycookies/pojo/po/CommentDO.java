package cn.mycookies.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDO {
    private Integer id;

    private String email;

    private Integer targetId;

    private String sessionId;

    private String replyEmail;

    private String content;

    private Integer likeCount;

    private Date createTime;

    private Date updateTime;

    private Integer commentStatus;

}
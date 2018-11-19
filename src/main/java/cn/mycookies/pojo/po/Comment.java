package cn.mycookies.pojo.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Comment {
    private Integer userId;

    private Integer targetId;

    private Integer parentId;

    private Integer replyUid;

    private String content;

    private Byte type;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String id;

}
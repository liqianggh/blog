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
public class Blog {
    private Integer id;

    private Integer categoryId;

    private String title;

    private String summary;

    private String author;

    private String tags;

    private Integer code;

    private String imgUrl;

    private Integer viewCount;

    private Integer likeCount;

    private Integer shareCount;

    private Integer commentCount;

    private Byte status;

    private Date createTime;

    private Date updateTime;

    private String content;

}
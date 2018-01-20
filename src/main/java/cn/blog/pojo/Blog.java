package cn.blog.pojo;

import lombok.*;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Blog {
    private Integer blogId;

    private Integer categoryId;

    private Integer code;

    private String title;

    private String author;

    private String content;

    //浏览量，点赞次数，分享次数，评论次数
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private Integer commentCount;

    private String imgUri;

    private Date createTime;

    private Date updateTime;

    private String tags;


}
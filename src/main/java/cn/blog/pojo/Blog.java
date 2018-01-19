package cn.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
    private Integer blogid;

    private Integer categoryid;

    private Integer code;

    private String title;

    private String author;

    private String content;

    //浏览量，点赞次数，分享次数，评论次数
    private Integer viewcount;
    private Integer likecount;
    private Integer sharecount;
    private Integer commentcount;

    private String imguri;

    private Date createtime;

    private Date updatetime;

    private String tags;


}
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

    private String title;

    private Integer categoryId;

    private String tags;

    private String summary;

    private String content;

    private Integer code;

    private String imgUrl;

    private String author;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Byte isDeleted;

    private Date createTime;

    private Date updateTime;

}
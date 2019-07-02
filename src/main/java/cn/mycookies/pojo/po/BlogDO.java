package cn.mycookies.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogDO {

    private Integer id;

    private Integer categoryId;

    private String title;

    private String summary;

    private String imgUrl;

    private String author;

    private Integer code;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Date createTime;

    private Date updateTime;

    private Byte blogStatus;

    private String content;
}
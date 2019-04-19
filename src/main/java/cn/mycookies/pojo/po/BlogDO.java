package cn.mycookies.pojo.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@ToString
public class BlogDO {
    private Integer id;

    private String title;

    private Integer categoryId;
    @JsonIgnore
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
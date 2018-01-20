package cn.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * @Description: po转换成vo时将日期格式化,新增imgHost
 * Created by Jann Lee on 2018/1/20  16:15.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class BlogVo {
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
    //图片服务器地址
    private String imgHost;

    private String createTimeStr;

    private String updateTimeStr;

    private String tags;
}

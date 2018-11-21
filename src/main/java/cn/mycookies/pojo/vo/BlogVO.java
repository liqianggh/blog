package cn.mycookies.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@ApiModel("评论回复实体类")
public class BlogVO {

    public Integer id;

    public String title;

    public String summary;

    public String content;

    private String imgUrl;

    private Integer code;

    private Integer viewCount;

    private Integer likeCount;

    public String createTime;

    public String updateTime;

    public String calcTime;

    private List<TagVO> tagList;
}

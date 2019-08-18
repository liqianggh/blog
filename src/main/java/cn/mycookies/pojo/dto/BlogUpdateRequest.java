package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 博客更新请求
 *
 * @author Jann Lee
 * @date 2019-07-02 23:03
 **/
@Data
public class BlogUpdateRequest {

    @ApiModelProperty(value = "分类id", required = true)
    private Integer categoryId;

    @Length(min = 1, max = 20, message = "title长度需在1-20之间")
    @NotEmpty
    @ApiModelProperty(value = "标题", required = true)
    private String title;

    @NotNull
    @Length(min = 1)
    @ApiModelProperty(value = "摘要", required = true)
    private String summary;

    @ApiModelProperty(value = "标签id")
    private List<Integer> tags;

    @ApiModelProperty(value = "封面图片url")
    private String imgUrl;

    @ApiModelProperty(value = "标识code", allowableValues = "0-普通博客，1-置顶, 2-推荐")
    private Integer code;

    @ApiModelProperty(value = "正文", required = true)
    private String content;

    @ApiModelProperty(value = "html格式正文", required = true)
    private String htmlContent;

    @ApiModelProperty(value = "博客目录", required = true)
    private String blogCatalog;

    @ApiModelProperty(value = "作为草稿，发布，删除", required = true)
    @NotNull
    private Byte status;

}

package cn.mycookies.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
@ApiModel("添加博客实体类")
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogDTO {
    @ApiModelProperty(value = "博客id",required = false,hidden = true)
    private Integer id;

    @Length(min = 1,max = 20,message = "title长度需在1-20之间")
    @ApiModelProperty(value="标题", required = true)
    private String title;

    @NotNull
    @ApiModelProperty(value="分类id", required = true)
    private Integer categoryId;

    @ApiModelProperty(value="标签id", required = false)
    private Integer[] tags;

    @ApiModelProperty(value="图片url", required = false)
    private String imgUrl;

    @NotNull
    @Length(min = 1)
    @ApiModelProperty(value="摘要", required = true)
    private String summary;

    @ApiModelProperty(value="正文", required = true)
    private String content;

    @ApiModelProperty(value="作者", required = false)
    private String author;
    /**
     *置顶 转载
     */
    @ApiModelProperty(value = "标识code",allowableValues = "0-普通博客，1-置顶")
    private Integer code;

    @ApiModelProperty(value = "作为草稿，发布，删除",required = true)
    private Integer isDeleted;

 }

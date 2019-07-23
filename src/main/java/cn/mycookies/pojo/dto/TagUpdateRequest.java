package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * 标签更新 请求
 *
 * @Author Jann Lee
 * @Date 2018-11-18 20:17
 **/
@ApiModel(value = "标签对象")
@Setter
@Getter
@ToString

public class TagUpdateRequest {

    @Length(min = 1,max = 10,message = "tagName长度需在1-10之间")
    @ApiModelProperty(value="标签名", required = true)
    private String tagName;

    @Length(max = 50,message = "tagDesc长度须在50之内")
    @ApiModelProperty(value = "标签描述")
    private String tagDesc;

    @ApiModelProperty(value = "标签类型",required = true)
    @NotNull
    private Integer tagType;
}

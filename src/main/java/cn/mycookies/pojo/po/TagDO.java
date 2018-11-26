package cn.mycookies.pojo.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("Tag实体类")
public class TagDO {

    @ApiModelProperty(name = "id",value = "标签的主键id",hidden = true)
    private Integer id;

    @Length(min = 1,max = 10,message = "tagName长度需在1-10之间")
    @ApiModelProperty(name = "id",value = "标签的主键id")
    private String tagName;

    @Length(max = 50,message = "tagDesc长度需50之间")
    private String tagDesc;

    @ApiModelProperty(value = "标签类型",required = false,hidden = true)
    private Byte type;

    @ApiModelProperty(hidden = true)
    private Date createTime;

    @ApiModelProperty(hidden = true)
    private Date updateTime;

}
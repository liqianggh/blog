package cn.mycookies.pojo.dto;

 import com.fasterxml.jackson.annotation.JsonInclude;
 import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
 import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName TagAddDTO
 * @Description TODO
 * @Author Jann Lee
 * @Date 2018-11-18 20:17
 **/
@ApiModel(value = "标签对象")
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagAddDTO {

    @Length(min = 1,max = 10,message = "tagName长度需在1-10之间")
    @ApiModelProperty(value="标签名", required = true)
    private String tagName;

    @Length(max = 50,message = "tagDesc长度须在50之内")
    @ApiModelProperty(value = "标签描述",required = false)
    private String tagDesc;

    @ApiModelProperty(value = "标签类型",required = false,dataType = "Byte")
    private Byte type;
}

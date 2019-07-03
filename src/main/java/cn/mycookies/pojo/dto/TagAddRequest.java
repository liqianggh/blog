package cn.mycookies.pojo.dto;

 import cn.mycookies.common.JsonBean;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 标签添加 请求
 *
 * @Author Jann Lee
 * @Date 2018-11-18 20:17
 **/
@ApiModel(value = "标签对象")
@Setter
@Getter
@ToString
@JsonBean
public class TagAddRequest extends TagUpdateRequest{

}

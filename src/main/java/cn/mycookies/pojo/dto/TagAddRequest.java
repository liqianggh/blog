package cn.mycookies.pojo.dto;

 import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 标签添加 请求
 *
 * @Author Jann Lee
 * @Date 2018-11-18 20:17
 **/
@ApiModel(value = "标签对象")
@Data
public class TagAddRequest extends TagUpdateRequest{

}

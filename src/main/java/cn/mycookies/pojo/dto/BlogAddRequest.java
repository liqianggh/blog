package cn.mycookies.pojo.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 字段修改与添加
 *
 * @author Jann Lee
 * @date 2019-06-30 22:20
 */
@ApiModel("添加博客实体类")
@Data
public class BlogAddRequest extends BlogUpdateRequest{

}

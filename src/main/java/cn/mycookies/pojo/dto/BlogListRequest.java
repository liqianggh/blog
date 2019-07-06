package cn.mycookies.pojo.dto;

import cn.mycookies.common.JsonBean;
import cn.mycookies.common.PageInfo4Request;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 博客查询参数bean
 *
 * @author Jann Lee
 * @date 2019-07-02 2:26
 **/
@ApiModel("博客列表查询参数")
@Data
@JsonBean
public class BlogListRequest extends PageInfo4Request {

    /**
     * 分类id
     */
    @ApiModelProperty(name = "分类id")
    private Integer categoryId;

    /**
     * 标签id
     */
    @ApiModelProperty(name = "标签id")
    private Integer tagId;

    /**
     * 状态吗
     */
    @ApiModelProperty(name = "状态")
    private Integer status;

}

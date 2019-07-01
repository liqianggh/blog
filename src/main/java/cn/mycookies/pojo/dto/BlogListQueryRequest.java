package cn.mycookies.pojo.dto;

import cn.mycookies.common.PageInfo4Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Jann Lee
 * @description 博客查询参数bean
 * @date 2019-07-02 2:26
 **/
@ApiModel("博客列表查询参数")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlogListQueryRequest extends PageInfo4Request {
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
    private Byte status;

}

package cn.mycookies.pojo.dto;

import cn.mycookies.common.basic.PageInfo4Request;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 标签列表查询接口
 *
 * @author Jann Lee
 * @date 2019-07-03 22:15
 **/

@Data
public class TagListRequest extends PageInfo4Request {
    /**
     * 标签类型
     */
    @JsonProperty(value = "tag_type")
    private Integer tagType;
}

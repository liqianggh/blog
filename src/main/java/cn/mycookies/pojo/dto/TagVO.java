package cn.mycookies.pojo.dto;

import cn.mycookies.pojo.po.TagDO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @ClassName TagVO
 * @Description 标签的bo类
 * @Author Jann Lee
 * @Date 2018-11-18 17:08
 **/
@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TagVO {

    private Integer id;

    private String tagName;

    private Integer tagType;

    private String tagDesc;

    private Long createTime;

    private Long updateTime;

    /**
     * 构建vo
     * @param tagDO
     * @return
     */
    public static TagVO createFrom(TagDO tagDO) {
        Preconditions.checkNotNull(tagDO, "构建vo时参数不能为null");
        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tagDO, tagVO);
        return tagVO;
    }
}

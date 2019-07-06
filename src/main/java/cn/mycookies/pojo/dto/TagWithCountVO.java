package cn.mycookies.pojo.dto;

import cn.mycookies.pojo.po.TagDO;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

/**
 * @author Jann Lee
 * @date 2019-07-06 19:06
 **/
@Setter
@Getter
@NoArgsConstructor
public class TagWithCountVO {

    private Integer id;

    private String tagName;

    private Integer blogCount;

    /**
     * 构建vo
     * @param tagDO
     * @return
     */
    public static TagWithCountVO createFrom(TagDO tagDO) {
        Preconditions.checkNotNull(tagDO, "构建vo时参数不能为null");
        TagWithCountVO tagVO = new TagWithCountVO();
        BeanUtils.copyProperties(tagDO, tagVO);
        return tagVO;
    }
}

package cn.mycookies.pojo.dto;

import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.utils.DateTimeUtil;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class TagVO {

    private Integer id;

    private String tagName;

    private Integer tagType;

    private String tagDesc;

    private String createTime;

    private String updateTime;

    /**
     * 构建vo
     * @param tagDO
     * @return
     */
    public static TagVO createFrom(TagDO tagDO) {
        Preconditions.checkNotNull(tagDO, "构建vo时参数不能为null");
        TagVO tagVO = new TagVO();
        BeanUtils.copyProperties(tagDO, tagVO);
        tagVO.setCreateTime(DateTimeUtil.dateToStr(tagDO.getCreateTime()));
        tagVO.setUpdateTime(DateTimeUtil.dateToStr(tagDO.getUpdateTime()));

        return tagVO;
    }
}

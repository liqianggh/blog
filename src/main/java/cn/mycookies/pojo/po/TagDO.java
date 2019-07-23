package cn.mycookies.pojo.po;

import lombok.Data;

/**
 * 标签do
 *
 * @author Jann Lee
 * @date 2019-07-03 22:34
 */
@Data
public class TagDO extends BaseDO{

    private String tagName;

    private String tagDesc;

    private Integer tagType;

}
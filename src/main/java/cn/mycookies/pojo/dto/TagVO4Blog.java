package cn.mycookies.pojo.dto;

import lombok.Data;

/**
 *  标签的bo类
 *
 * @author Jann Lee
 * @date 2018-11-18 17:08
 **/
@Data
public class TagVO4Blog {

    private Integer id;

    private String tagName;

    private Byte type;

    /**
     * 标签描述
     */
    private String tagDesc;

    private String createTime;

    private String updateTime;


}

package cn.mycookies.pojo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

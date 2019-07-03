package cn.mycookies.pojo.po;

import lombok.Data;

/**
 * do类的基类
 *
 * @author Jann Lee
 * @date 2019-07-03 23:34
 **/
@Data
public class BaseDO {

    private Integer id;

    private Long createTime;

    private Long updateTime;
}

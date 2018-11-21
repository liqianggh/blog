package cn.mycookies.pojo.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @ClassName TagVO
 * @Description TODO
 * @Author Jann Lee
 * @Date 2018-11-21 0:02
 **/
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TagVO {

    public Integer id;

    public String tagName;

    public Integer count;
}

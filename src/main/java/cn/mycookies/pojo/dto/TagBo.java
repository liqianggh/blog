package cn.mycookies.pojo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @ClassName TagBo
 * @Description 标签的bo类
 * @Author Jann Lee
 * @Date 2018-11-18 17:08
 **/
@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TagBo {

    private Integer id;

    private String tagName;

    /**
     * 标签描述
     */
    private String tagDesc;
    /**
     * 引用当前标签的文章数量
     */
    private Integer count;

    private String createTime;
    private String updateTime;


}

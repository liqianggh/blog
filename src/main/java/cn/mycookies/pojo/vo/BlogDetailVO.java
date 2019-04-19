package cn.mycookies.pojo.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 博客详情vo
 * @author Jann Lee
 * @description TODO
 * @date 2019-04-19 23:34
 **/
@Data
@Builder
public class BlogDetailVO {

    public Integer id;

    public String title;

    public String summary;

    public String content;

    public String imgUrl;

    public Integer code;

    public Integer categoryId;

    public List<Integer> tags;
}

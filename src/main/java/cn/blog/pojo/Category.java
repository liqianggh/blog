package cn.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
 /**
  * @Description: 博客分类，使用lombok简化代码
  * Created by Jann Lee on 2018/1/19  23:25.
  */
 @Setter
 @Getter
 @NoArgsConstructor
 @AllArgsConstructor
public class Category {
    private Integer categoryid;

    private String categoryname;

    private String categorydesc;

    private Date createtime;

    private Date updatetime;

}
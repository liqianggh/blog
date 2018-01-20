package cn.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JacksonInject;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;
 /**
  * @Description: 博客分类，使用lombok简化代码
  * Created by Jann Lee on 2018/1/19  23:25.
  */
 @Setter
 @Getter
 @NoArgsConstructor
 @AllArgsConstructor
 @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Category {
    private Integer categoryId;

    private String categoryName;

    private String categoryDesc;

    private Date createTime;

    private Date updateTime;

}
package cn.blog.bo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;

 /**
  * @Description: 业务对象
  * Created by Jann Lee on 2018/1/21  18:29.
  */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TagsAndBlog {
    private Integer blogId;
    private Integer tagId;
    private Integer id;
}

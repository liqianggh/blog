package cn.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

 /**
  * @Description: po转换成vo时将日期格式化
  * Created by Jann Lee on 2018/1/20  16:15.
  */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CategoryVo {

    private Integer categoryId;

    private String categoryName;

    private String categoryDesc;

    private String createTimeStr;

    private String updateTimeStr;

    private Integer blogCount;


}

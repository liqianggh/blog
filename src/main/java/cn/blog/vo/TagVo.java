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
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class TagVo {
    private Integer tagId;

    private String tagName;

    private String createTimeStr;

    private String updateTimeStr;

}

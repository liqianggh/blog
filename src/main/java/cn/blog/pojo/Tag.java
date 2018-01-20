package cn.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
 public class Tag {
    private Integer tagId;

    private String tagName;

    private Date createTime;

    private Date updateTime;

}
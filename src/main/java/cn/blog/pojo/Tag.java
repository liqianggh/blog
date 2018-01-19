package cn.blog.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Integer tagid;

    private String tagname;

    private Date createtime;

    private Date updatetime;

}
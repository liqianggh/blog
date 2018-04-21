package cn.blog.pojo;

import lombok.*;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Visitor {
    private Integer visitorid;

    private String ipaddr;

    private Date createtime;

    private Date updatetime;


}
package cn.mycookies.pojo.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Setter
@Getter
@NoArgsConstructor
@ToString
public class Category {
    private Integer id;

    private String categoryName;

    private String categoryDesc;

    private Date createTime;

    private Date updateTime;

}
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
public class Tag {
    private Integer id;

    private String tagName;

    private String tagDesc;

    private Date createTime;

    private Date updateTime;
}
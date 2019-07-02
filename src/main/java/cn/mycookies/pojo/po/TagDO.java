package cn.mycookies.pojo.po;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
@NotNull
public class TagDO {
    private Integer id;

    private String tagName;

    private String tagDesc;

    private Byte type;

    private Date createTime;

    private Date updateTime;
}
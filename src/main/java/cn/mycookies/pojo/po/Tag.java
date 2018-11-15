package cn.mycookies.pojo.po;

import java.util.Date;

public class Tag {
    private Integer id;

    private String tagName;

    private String tagDesc;

    private Date createTime;

    private Date updateTime;

    public Tag(Integer id, String tagName, String tagDesc, Date createTime, Date updateTime) {
        this.id = id;
        this.tagName = tagName;
        this.tagDesc = tagDesc;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public String getTagName() {
        return tagName;
    }

    public String getTagDesc() {
        return tagDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
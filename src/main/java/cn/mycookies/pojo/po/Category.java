package cn.mycookies.pojo.po;

import java.util.Date;

public class Category {
    private Integer id;

    private String categoryName;

    private String categoryDesc;

    private Date createTime;

    private Date updateTime;

    public Category(Integer id, String categoryName, String categoryDesc, Date createTime, Date updateTime) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryDesc = categoryDesc;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
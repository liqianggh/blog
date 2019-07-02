package cn.mycookies.pojo.po;

import java.util.Date;

public class VisitorDO {
    private Integer id;

    private Integer ipAddress;

    private Date createTime;

    private Date updateTime;

    public VisitorDO(Integer id, Integer ipAddress, Date createTime, Date updateTime) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public Integer getIpAddress() {
        return ipAddress;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
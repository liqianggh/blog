package cn.mycookies.pojo.po;

import java.util.Date;

public class Comment {
    private Integer userId;

    private Integer targetId;

    private Integer parentId;

    private Integer replyUid;

    private String content;

    private Byte type;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String id;

    public Comment(Integer userId, Integer targetId, Integer parentId, Integer replyUid, String content, Byte type, Integer status, Date createTime, Date updateTime) {
        this.userId = userId;
        this.targetId = targetId;
        this.parentId = parentId;
        this.replyUid = replyUid;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Comment(Integer userId, Integer targetId, Integer parentId, Integer replyUid, String content, Byte type, Integer status, Date createTime, Date updateTime, String id) {
        this.userId = userId;
        this.targetId = targetId;
        this.parentId = parentId;
        this.replyUid = replyUid;
        this.content = content;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public Integer getReplyUid() {
        return replyUid;
    }

    public String getContent() {
        return content;
    }

    public Byte getType() {
        return type;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getId() {
        return id;
    }
}
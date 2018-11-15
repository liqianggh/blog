package cn.mycookies.pojo.po;

import java.util.Date;

public class Blog {
    private Integer id;

    private Integer categoryId;

    private String title;

    private String summary;

    private String author;

    private String tags;

    private Integer code;

    private String imgUrl;

    private Integer viewCount;

    private Integer likeCount;

    private Integer shareCount;

    private Integer commentCount;

    private Byte status;

    private Date createTime;

    private Date updateTime;

    private String content;

    public Blog(Integer id, Integer categoryId, String title, String summary, String author, String tags, Integer code, String imgUrl, Integer viewCount, Integer likeCount, Integer shareCount, Integer commentCount, Byte status, Date createTime, Date updateTime) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.summary = summary;
        this.author = author;
        this.tags = tags;
        this.code = code;
        this.imgUrl = imgUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.shareCount = shareCount;
        this.commentCount = commentCount;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Blog(Integer id, Integer categoryId, String title, String summary, String author, String tags, Integer code, String imgUrl, Integer viewCount, Integer likeCount, Integer shareCount, Integer commentCount, Byte status, Date createTime, Date updateTime, String content) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.summary = summary;
        this.author = author;
        this.tags = tags;
        this.code = code;
        this.imgUrl = imgUrl;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.shareCount = shareCount;
        this.commentCount = commentCount;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getAuthor() {
        return author;
    }

    public String getTags() {
        return tags;
    }

    public Integer getCode() {
        return code;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public Byte getStatus() {
        return status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public String getContent() {
        return content;
    }
}
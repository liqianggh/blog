package cn.mycookies.pojo.po;

import java.util.Date;

public class PhotoDO {
    private Short id;

    private Short albumId;

    private String imgUrl;

    private String alt;

    private Date createTime;

    private Date updateTime;

    public PhotoDO(Short id, Short albumId, String imgUrl, String alt, Date createTime, Date updateTime) {
        this.id = id;
        this.albumId = albumId;
        this.imgUrl = imgUrl;
        this.alt = alt;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Short getId() {
        return id;
    }

    public Short getAlbumId() {
        return albumId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getAlt() {
        return alt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
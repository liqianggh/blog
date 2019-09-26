package cn.mycookies.pojo.meta;

import java.util.Date;

public class AlbumDO {
    private Short id;

    private String name;

    private String albumDesc;

    private String coverImg;

    private Date createTime;

    private Date updateTime;

    public AlbumDO(Short id, String name, String albumDesc, String coverImg, Date createTime, Date updateTime) {
        this.id = id;
        this.name = name;
        this.albumDesc = albumDesc;
        this.coverImg = coverImg;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Short getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAlbumDesc() {
        return albumDesc;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
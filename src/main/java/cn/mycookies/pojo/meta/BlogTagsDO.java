package cn.mycookies.pojo.meta;

public class BlogTagsDO {
    private Integer id;

    private Integer tagId;

    private Integer blogId;

    public BlogTagsDO(Integer id, Integer tagId, Integer blogId) {
        this.id = id;
        this.tagId = tagId;
        this.blogId = blogId;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public Integer getBlogId() {
        return blogId;
    }
}
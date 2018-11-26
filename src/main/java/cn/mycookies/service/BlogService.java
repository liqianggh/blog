package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import com.github.pagehelper.PageInfo;

public interface BlogService {
    /**
     * 添加博客
     * @param blogAdd
     * @return
     */
    ServerResponse insertBlog(BlogDTO blogAdd);

    /**
     * 查询博客列表
     * @param pageNum
     * @param pageSize
     * @param categoryId 分类id
     * @param tagId 标签id
     * @param idDeleted 是否呗逻辑删除或存入草稿
     * @param orderby 排序条件 如 createTime desc
     * @return
     */
    ServerResponse<PageInfo<BlogVO>> listBlogs(Integer pageNum, Integer pageSize, Integer categoryId, Integer tagId, Byte idDeleted, String orderby);

    /**
     * 更新博客
     * @param blogAdd
     * @return
     */
    ServerResponse updateBlog(BlogDTO blogAdd);

    /**
     * 获取一篇博客
     * @param id 博客的id
     * @param isDeleted 是狗北放入回收站
     * @param hashLastNext 是否包含上一篇和下一篇
     * @return
     */
    ServerResponse<BlogVO> getBlog(Integer id, Byte isDeleted,boolean hashLastNext,boolean hasComments);

    /**
     * 点赞 浏览量 统计
     * @param id
     * @param type
     * @return
     */
    ServerResponse updateBlogCount(Integer id, String type);

    /**
     * 获取首页VO
     * @return
     */
    ServerResponse<IndexVO> getIndexVO();
}

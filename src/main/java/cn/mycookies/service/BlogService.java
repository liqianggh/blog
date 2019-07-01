package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogAddRequest;
import cn.mycookies.pojo.dto.BlogListQueryRequest;
import cn.mycookies.pojo.po.BlogDO;
import cn.mycookies.pojo.vo.BlogDetailVO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;

public interface BlogService {
    /**
     * 添加博客
     * @param blogAddRequest
     * @return
     */
    ServerResponse addBlog(BlogAddRequest blogAddRequest);

    /**
     * 查询博客列表
     * @return
     * @param queryRequest
     */
    ServerResponse<PageInfo<BlogVO>> getBlogListInfos(BlogListQueryRequest queryRequest);

    /**
     * 更新博客
     * @param blogAddRequest
     * @return
     */
    ServerResponse updateBlog(BlogAddRequest blogAddRequest);

    /**
     * 获取一篇博客
     * @param id 博客的id
     * @param isDeleted 是狗北放入回收站
     * @param hashLastNext 是否包含上一篇和下一篇
     * @return
     */
    ServerResponse<BlogVO> getBlog(Integer id, Byte isDeleted,boolean hashLastNext,boolean hasComments);

     ServerResponse<BlogDO> getBlogById(Integer id);


        /**
         * 点赞 浏览量 统计
         * @param id
         * @param type
         * @return
         */
    ServerResponse updateBlogCount(Integer id, String type);

    /**
     * 获取首页VO
     * @param withBlogs 结果集是否包含博客列表
     * @return
     */
    ServerResponse<IndexVO> getIndexVO(boolean withBlogs);

    ServerResponse<BlogDetailVO> getBlogDetailVOById(@Param("blogId") Integer blogId);
}

package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import com.github.pagehelper.PageInfo;

public interface BlogService {
    ServerResponse addBlog(BlogDTO blogAdd);

    ServerResponse<PageInfo<BlogVO>> getBlogs(Integer pageNum, Integer pageSize, Integer categoryId, Integer tagId, Byte idDeleted,String orderby);

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
    ServerResponse blogCountPlus(Integer id, String type);

    ServerResponse<IndexVO> getIndexVO();
}

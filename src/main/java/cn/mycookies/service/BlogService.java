package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.vo.BlogVO;
import com.github.pagehelper.PageInfo;

public interface BlogService {
    ServerResponse addBlog(BlogDTO blogAdd);

    ServerResponse<PageInfo<BlogVO>> getBlogs(Integer pageNum, Integer pageSize, Byte categoryId, Byte tagId, Integer idDeleted,String orderby);

    ServerResponse updateBlog(BlogDTO blogAdd);

    ServerResponse<BlogVO> getBlog(Integer id, Byte all);
}

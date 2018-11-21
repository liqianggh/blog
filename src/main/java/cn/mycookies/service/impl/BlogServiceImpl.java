package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.po.Blog;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.service.BlogService;
import cn.mycookies.service.TagService;
import cn.mycookies.utils.DateCalUtils;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TagService tagService;

    @Override
    public ServerResponse addBlog(BlogDTO blogAdd) {
        return null;
    }

    @Override
    public ServerResponse<PageInfo<BlogVO>> getBlogs(Integer pageNum, Integer pageSize, Byte categoryId, Byte tagId, Integer isDeleted, String orderBy) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        String[] strs = orderBy.trim().split(" ");
        page.setOrderBy(strs[0] + " " + strs[1]);

        List<Blog> blogList = blogMapper.selectBlogs(categoryId, tagId, isDeleted);

        List<BlogVO> blogVOList = Lists.newArrayList();
        blogList.stream().forEach(blog -> {
            BlogVO blogVO = convertBlogToVO(blog);
            blogVO.setTagList(tagService.geteTagsOfBlog(blog.getId()));
            blogVOList.add(blogVO);
        });
        if (blogList.size() == 0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(blogVOList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse updateBlog(BlogDTO blogAdd) {
        return null;
    }

    @Override
    public ServerResponse<BlogVO> getBlog(Integer id, Byte isDeleted) {
        if (isDeleted == DataStatus.ALL) {
            isDeleted = null;
        }
        Blog blog = blogMapper.selectById(id, isDeleted);
        if (Objects.isNull(blog)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        BlogVO blogVO = convertBlogToVO(blog);
        blogVO.setTagList(tagService.geteTagsOfBlog(id));

        return ServerResponse.createBySuccess(blogVO);
    }

    private BlogVO convertBlogToVO(Blog blog) {
        BlogVO blogVo = new BlogVO();
        blogVo.setCalcTime(DateCalUtils.format(blog.getCreateTime()));
        blogVo.setContent(blog.getContent());
        blogVo.setCreateTime(DateTimeUtil.dateToStr(blog.getCreateTime(), DateTimeUtil.DATE_FORMAT));
        blogVo.setId(blog.getId());
        blogVo.setSummary(blog.getSummary());
        blogVo.setImgUrl(blog.getImgUrl());
        blogVo.setViewCount(blog.getViewCount());
        blogVo.setLikeCount(blog.getLikeCount());
        blogVo.setTitle(blog.getTitle());
        blogVo.setCode(blog.getCode());
        return blogVo;
    }
}

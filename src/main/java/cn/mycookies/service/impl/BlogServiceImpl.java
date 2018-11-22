package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.po.Blog;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.service.BlogService;
import cn.mycookies.service.CommentService;
import cn.mycookies.service.TagService;
import cn.mycookies.utils.DateCalUtils;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Objects;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Override
    public ServerResponse addBlog(BlogDTO blogAdd) {
        if (Objects.isNull(blogAdd)  || StringUtils.isEmpty(blogAdd.getTitle())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(),ActionStatus.PARAMAS_ERROR.getDescription());
        }
        // 校验title是否又重复的
        int result = blogMapper.insertSelective(blogAdd);

        if (result ==0 ) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(),ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess();
     }

    @Override
    public ServerResponse<PageInfo<BlogVO>> getBlogs(Integer pageNum, Integer pageSize, Integer categoryId, Integer tagId, Byte isDeleted,String orderby) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        String[] strs = orderby.trim().split(" ");
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
        if (Objects.isNull(blogAdd) || Objects.isNull(blogAdd.getId()) || blogAdd.getId()==0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(),ActionStatus.PARAMAS_ERROR.getDescription());
        }
        int result = blogMapper.updateBlog(blogAdd);
        if (result ==0 ) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(),ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess();
    }

    @Override
    public ServerResponse<BlogVO> getBlog(Integer id, Byte isDeleted,boolean hasLastNext,boolean hasComments) {
        if (isDeleted == DataStatus.ALL) {
            isDeleted = null;
        }
        Blog blog = blogMapper.selectById(id, isDeleted);
        if (Objects.isNull(blog)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        BlogVO blogVO = convertBlogToVO(blog);
        blogVO.setTagList(tagService.geteTagsOfBlog(id));
        // 查询last next
        if (hasLastNext) {
            blogVO.setLast(getlastOrNext(id,-1));
            blogVO.setNext(getlastOrNext(id,1));
        }
        //查询 评论列表
        if (hasComments) {
            blogVO.setComments(commentService.getComments(1,10,null,null,id,null,DataStatus.NO_DELETED).getData());
        }

        return ServerResponse.createBySuccess(blogVO);
    }

    @Override
    public ServerResponse blogCountPlus(Integer id, String type) {
        Blog blog = blogMapper.selectById(id, DataStatus.NO_DELETED);
        if (Objects.isNull(blog)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(),ActionStatus.PARAMAS_ERROR.getDescription());
        }
        int result = blogMapper.updateBlogCount(id,type);
        return null;
    }

    private BlogDTO getlastOrNext(Integer id, Integer page){
        Blog blog = blogMapper.selectLastOrNext(id,page);
        if (Objects.isNull(blog)){
            return null;
        }
        BlogDTO result = new BlogDTO();
        result.setId(blog.getId());
        result.setTitle(blog.getTitle());

        return result;
    }

    private BlogVO convertBlogToVO(Blog blog) {
        BlogVO blogVo = new BlogVO();
        blogVo.setCalcTime(DateCalUtils.format(blog.getCreateTime()));
        blogVo.setContent(blog.getContent());
        blogVo.setCreateTime(DateTimeUtil.dateToStr(blog.getCreateTime(), DateTimeUtil.DATE_FORMAT));
        blogVo.setUpdateTime(DateTimeUtil.dateToStr(blog.getUpdateTime()));
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

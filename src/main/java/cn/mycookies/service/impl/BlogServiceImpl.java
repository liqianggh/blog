package cn.mycookies.service.impl;

import cn.mycookies.common.*;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.dao.BlogTagsDOMapper;
import cn.mycookies.pojo.dto.BlogDTO;
import cn.mycookies.pojo.po.BlogDO;
import cn.mycookies.pojo.po.BlogTagsDO;
import cn.mycookies.pojo.po.BlogTagsDOExample;
import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.vo.BlogDetailVO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import cn.mycookies.pojo.vo.TagVO;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogTagsDOMapper blogTagsDOMapper;

    @Override
    public ServerResponse insertBlog(BlogDTO blogAdd) {
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
    public ServerResponse<PageInfo<BlogVO>> listBlogs(Integer pageNum, Integer pageSize, Integer categoryId, Integer tagId, Byte isDeleted, String orderby) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        String[] strs = orderby.trim().split(" ");
        page.setOrderBy(strs[0] + " " + strs[1]);

        List<BlogDO> blogDOList = blogMapper.selectBlogs(categoryId, tagId, isDeleted);

        List<BlogVO> blogVOList = Lists.newArrayList();
        blogDOList.stream().forEach(blogDO -> {
            BlogVO blogVO = convertBlogToVO(blogDO);
            blogVO.setTagList(tagService.listTagsOfBlog(blogDO.getId()));
            blogVOList.add(blogVO);
        });
        if (blogDOList.size() == 0) {
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
        Integer id = blogAdd.getId();
        List<Integer> tagIds = blogAdd.getTags();
        BlogTagsDOExample example = new BlogTagsDOExample();
        BlogTagsDOExample.Criteria criteria = example.createCriteria();
        criteria.andBlogIdEqualTo(id);
        criteria.andTagIdIn(tagIds);
        blogTagsDOMapper.deleteByExample(example);
        for(Integer tagId : tagIds ){
            blogTagsDOMapper.insert(new BlogTagsDO(null, tagId, id));
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
        BlogDO blogDO = blogMapper.selectById(id, isDeleted);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        BlogVO blogVO = convertBlogToVO(blogDO);
        blogVO.setTagList(tagService.listTagsOfBlog(id));
        // 查询last next
        if (hasLastNext) {
            blogVO.setLast(getlastOrNext(id,-1));
            blogVO.setNext(getlastOrNext(id,1));
        }
        //查询 评论列表
        if (hasComments) {
            blogVO.setComments(commentService.listComments(1,10,null,null,id,null,DataStatus.NO_DELETED).getData());
        }

        return ServerResponse.createBySuccess(blogVO);
    }

    @Override
    public ServerResponse<BlogDO> getBlogById(Integer id) {

        BlogDO blogDO = blogMapper.selectById(id,null);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess(blogDO);
    }

    @Override
    public ServerResponse updateBlogCount(Integer id, String type) {
        BlogDO blogDO = blogMapper.selectById(id, DataStatus.NO_DELETED);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(),ActionStatus.PARAMAS_ERROR.getDescription());
        }
        int result = blogMapper.updateBlogCount(id,type);
        if (result == 0) {
            return ServerResponse.createByError();
        } else {
            return ServerResponse.createBySuccess();
        }
    }

    @Override
    public ServerResponse<IndexVO> getIndexVO(boolean withBlogs) {
        PageInfo<BlogVO> blogs = null;
        List<BlogVO> recommendList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.RECOMMEND_BLOG,5);

        List<BlogVO> clickRankList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.HOT_BLOG,5);

        List<TagVO> tagVOS = tagService.listTagVOs(TagTypes.TAG_LABEL).getData();
        List<TagVO> categoryVOS = tagService.listTagVOs(TagTypes.TAG_CATEGORY).getData();

        IndexVO indexVO = new IndexVO();
        if (withBlogs) {
            blogs = listBlogs(1,10,null,null,DataStatus.NO_DELETED,"create_time desc").getData();
            indexVO.setBlogList(blogs);
        }
        indexVO.setCategoryList(categoryVOS);
        indexVO.setTagList(tagVOS);
        indexVO.setRecommendList(recommendList);
        indexVO.setClickRankList(clickRankList);
        return ServerResponse.createBySuccess(indexVO);
    }

    @Override
    public ServerResponse<BlogDetailVO> getBlogDetailVOById(Integer blogId) {
        BlogDO blogDO = blogMapper.selectById(blogId, null);
        List<Integer> tagIds= tagService.listTagsOfBlog(blogId).stream().map(TagVO::getId).collect(Collectors.toList());
        BlogDetailVO blogDetailVO = BlogDetailVO.builder().categoryId(blogDO.getCategoryId())
                .code(blogDO.getCode())
                .content(blogDO.getContent())
                .id(blogDO.getId())
                .imgUrl(blogDO.getImgUrl())
                .summary(blogDO.getSummary())
                .tags(tagIds)
                .title(blogDO.getTitle()).build();

        return ServerResponse.createBySuccess(blogDetailVO);
    }

    private BlogDTO getlastOrNext(Integer id, Integer page){
        BlogDO blogDO = blogMapper.selectLastOrNext(id,page);
        if (Objects.isNull(blogDO)){
            return null;
        }
        BlogDTO result = new BlogDTO();
        result.setId(blogDO.getId());
        result.setTitle(blogDO.getTitle());

        return result;
    }

    private BlogVO convertBlogToVO(BlogDO blogDO) {
        BlogVO blogVo = new BlogVO();
        blogVo.setCalcTime(DateCalUtils.format(blogDO.getCreateTime()));
        blogVo.setContent(blogDO.getContent());
        blogVo.setCreateTime(DateTimeUtil.dateToStr(blogDO.getCreateTime(), DateTimeUtil.DATE_FORMAT));
        blogVo.setUpdateTime(DateTimeUtil.dateToStr(blogDO.getUpdateTime()));
        blogVo.setId(blogDO.getId());
        blogVo.setSummary(blogDO.getSummary());
        blogVo.setImgUrl(blogDO.getImgUrl());
        blogVo.setViewCount(blogDO.getViewCount());
        blogVo.setLikeCount(blogDO.getLikeCount());
        blogVo.setTitle(blogDO.getTitle());
        blogVo.setCode(blogDO.getCode());
        blogVo.setCategoryId(blogDO.getCategoryId());
        return blogVo;
    }
}

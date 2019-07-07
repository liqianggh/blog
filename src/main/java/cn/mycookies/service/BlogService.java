package cn.mycookies.service;

import cn.mycookies.common.*;
import cn.mycookies.common.exception.BusinessException;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.dao.BlogTagsDOMapper;
import cn.mycookies.pojo.dto.*;
import cn.mycookies.pojo.po.*;
import cn.mycookies.pojo.vo.BlogDetailVO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import cn.mycookies.utils.DateCalUtils;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogService extends BaseService {

    @Resource
    private BlogMapper blogMapper;

    @Resource
    private BlogTagsDOMapper blogTagsDOMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;


    /**
     * 添加新的博客
     *
     * @param addRequest 添加数据请求
     * @return
     */
    public ServerResponse createBlogInfo(BlogAddRequest addRequest) {
        BlogDO blogDO = new BlogDO();
        // 校验参数
        ServerResponse<Boolean> validateResult = validateAndInitCreateRequest(addRequest, blogDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        if ( blogMapper.insertSelective(blogDO) == 0) {
            return resultError4DB();
        }
        Integer blogId = blogDO.getId();
        List<Integer> tags = addRequest.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            tagService.deleteTagsByBlogId(blogId);
            tagService.createBlogTags(blogId, tags);
        }
        return resultOk();
    }

    /**
     * 更新博客
     *
     * @param id            主鍵id
     * @param updateRequest 更新请求参数
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public ServerResponse<Boolean> updateBlog(Integer id, BlogUpdateRequest updateRequest) {
        Preconditions.checkNotNull(id, "更新时id参数不能为null");
        BlogDO blogDO = blogMapper.selectByIdAndStatus(id, null);
        ServerResponse<Boolean> validateResult = validateAndInitUpdateRequest(updateRequest, blogDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }

        List<Integer> tagIds = updateRequest.getTags();
        // 删除旧的标签关联
        BlogTagsDOExample example = new BlogTagsDOExample();
        BlogTagsDOExample.Criteria criteria = example.createCriteria();
        criteria.andBlogIdEqualTo(id);
        blogTagsDOMapper.deleteByExample(example);
        // 添加新的标签关联
        for (Integer tagId : tagIds) {
            if (blogTagsDOMapper.insert(new BlogTagsDO(null, tagId, id)) == 0) {
                throw new BusinessException(ActionStatus.DATABASE_ERROR.inValue(), "添加标签过程失败");
            }
        }
        if (blogMapper.updateByPrimaryKey(blogDO) == 0) {
            throw new BusinessException(ActionStatus.DATABASE_ERROR.inValue(), "更新过程失败");
        }
        return resultOk();
    }


    /**
     * 校验添加参数，然后规整参数
     *
     * @param addRequest 添加请求信息
     * @param blogDO     要添加的do
     * @return
     */
    private ServerResponse<Boolean> validateAndInitCreateRequest(BlogAddRequest addRequest, BlogDO blogDO) {
        Preconditions.checkNotNull(blogDO, "添加参数不能为null");
        fillCreateTime(blogDO);
        return validateAndInitUpdateRequest(addRequest, blogDO);
    }

    /**
     * 校验更新参数， 然后规整参数
     *
     * @param updateRequest
     * @param blogDO
     * @return
     */
    private ServerResponse<Boolean> validateAndInitUpdateRequest(BlogUpdateRequest updateRequest, BlogDO blogDO) {
        Preconditions.checkNotNull(updateRequest, "更新参数不能为null");
        if (Objects.isNull(blogDO)) {
            return resultError4Param("数据不存在");
        }
        // 标题是否存在/发生变化
        String newTitle = updateRequest.getTitle();
        String oldTitle = blogDO.getTitle();
        BlogExample blogDOExample = new BlogExample();
        blogDOExample.createCriteria().andTitleEqualTo(newTitle);
        if (Objects.equals(newTitle, oldTitle) || CollectionUtils.isNotEmpty(blogMapper.selectByExample(blogDOExample))) {
            resultError4Param("标题[" + updateRequest.getTitle() + "]已存在");
        }
        // 分类是否存在
        Integer categoryId = updateRequest.getCategoryId();
        if (Objects.nonNull(categoryId)) {
            if (CollectionUtils.isEmpty(tagService.getTagListByTypeAndIds(TagType.CATEGORY, Lists.newArrayList(categoryId)))) {
                return resultError4Param("分类信息不存在");
            }
        }
        // 标签是否存在
        List<Integer> tags = updateRequest.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            List<TagDO> tagDOS = tagService.getTagListByTypeAndIds(TagType.TAG, Lists.newArrayList(tags));
            if (CollectionUtils.isNotEmpty(tagDOS)) {
                updateRequest.setTags(tagDOS.stream().map(TagDO::getId).collect(Collectors.toList()));
            }
        }
        BeanUtils.copyProperties(updateRequest, blogDO);
        // todo  code、status规整
        fillUpdateTime(blogDO);
        blogDO.setBlogStatus(updateRequest.getStatus());
        if (Objects.isNull(blogDO)) {
            return resultError4Param("当前数据不存在");
        }
        return resultOk();
    }

    /**
     * 分页查询博客
     *
     * @param queryRequest 查询条件
     * @return
     */
    public ServerResponse<PageInfo<BlogVO>> getBlogListInfos(BlogListRequest queryRequest) {
        Preconditions.checkNotNull(queryRequest, "查询参数不能为null");
        Page page = getPage(queryRequest);

        List<BlogDO> blogDOList = blogMapper.selectBlogs(queryRequest);

        List<BlogVO> blogVOList = blogDOList.stream().map(blogDO -> {
            BlogVO blogVO = convertBlogToVO(blogDO);
            blogVO.setTagList(tagService.getTagListByBlogId(blogDO.getId()));
            return blogVO;
        }).collect(Collectors.toList());

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(blogVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 获取博客详情
     *
     * @param id
     * @return
     */
    public ServerResponse<BlogVO> getBlogDetailsInfo(Integer id) {
        BlogDO blogDO = blogMapper.selectByPrimaryKey(id);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("数据不存在");
        }
        BlogVO blogVO = convertBlogToVO(blogDO);
        // 博客的标签
        blogVO.setTagList(tagService.getTagListByBlogId(id));
        // 查询last next
        blogVO.setLast(getlastOrNext(id, false));
        blogVO.setNext(getlastOrNext(id, true));
        //查询 评论列表
        CommentListRequest commentListRequest = new CommentListRequest();
        commentListRequest.setTargetType(CommentTargetType.ARTICLE);
        blogVO.setComments(commentService.getCommentInfos(id, commentListRequest).getData());

        return ServerResponse.createBySuccess(blogVO);
    }

    /**
     * 根据id获取博客
     * @param id
     * @return
     */
    public BlogDO getBlogById(Integer id) {

        if (Objects.isNull(id)) {
            return null;
        }
       return blogMapper.selectByIdAndStatus(id, null);
    }

    public ServerResponse updateBlogCount(Integer id, String type) {
        BlogDO blogDO = blogMapper.selectByIdAndStatus(id, DataStatus.NO_DELETED);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        }
        int result = blogMapper.updateBlogCount(id, type);
        if (result == 0) {
            return ServerResponse.createByError();
        } else {
            return ServerResponse.createBySuccess();
        }
    }

    /**
     * todo 优化
     * 获取首页所需数据
     *
     * @param withBlogs 结果集是否包含博客列表
     * @return
     */
    public ServerResponse<IndexVO> getIndexVO(boolean withBlogs) {
        PageInfo<BlogVO> blogs = null;
        /**
         * 查询推荐或热门博客
         */
        List<BlogVO> recommendList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.RECOMMEND_BLOG, 5);
        List<BlogVO> clickRankList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.HOT_BLOG, 5);
        // 标签分类信息
        List<TagWithCountVO> tagVOS = tagService.getTagListByTagType(TagType.TAG);
        List<TagWithCountVO> categoryVOS = tagService.getTagListByTagType(TagType.CATEGORY);
        // 博客内容
        IndexVO indexVO = new IndexVO();
        if (withBlogs) {
            BlogListRequest blogListRequest = new BlogListRequest();
            blogListRequest.setStatus(YesOrNoType.YES.getCode());
            blogs = getBlogListInfos(blogListRequest).getData();
            indexVO.setBlogList(blogs);
        }
        indexVO.setCategoryList(categoryVOS);
        indexVO.setTagList(tagVOS);
        indexVO.setRecommendList(recommendList);
        indexVO.setClickRankList(clickRankList);
        return ServerResponse.createBySuccess(indexVO);
    }

    /**
     * 查询博客详情
     *
     * @param blogId
     * @return
     */
    public ServerResponse<BlogDetailVO> getBlogDetailInfo(Integer blogId) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");

        BlogDO blogDO = blogMapper.selectByPrimaryKey(blogId);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("博客不存在");
        }
        // 获取博客的标签
        List<Integer> tagIds = tagService.getTagListByBlogId(blogId).stream().map(TagVO::getId).collect(Collectors.toList());
        return resultOk(BlogDetailVO.createFrom(blogDO, tagIds));
    }

    /**
     * 获取博客的上一篇或者下一篇
     *
     * @param id     当前博客主键id
     * @param isLast 是否是上一篇
     * @return
     */
    private BlogDetailVO getlastOrNext(Integer id, boolean isLast) {
        Preconditions.checkNotNull(id, "主键id不能为null");
        BlogDO blogDO = blogMapper.selectLastOrNext(id, isLast);
        if (Objects.isNull(blogDO)) {
            return null;
        }
        return BlogDetailVO.createFrom(blogDO, null);
    }

    private BlogVO convertBlogToVO(BlogDO blogDO) {
        BlogVO blogVo = new BlogVO();
        blogVo.setCalcTime(DateCalUtils.format(new Date(blogDO.getCreateTime())));
        blogVo.setContent(blogDO.getContent());
        blogVo.setCreateTime(DateTimeUtil.dateToStr(blogDO.getCreateTime()));
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

package cn.mycookies.service;

import cn.mycookies.common.*;
import cn.mycookies.common.exception.BusinessException;
import cn.mycookies.dao.BlogDOMapper;
import cn.mycookies.dao.BlogTagsDOMapper;
import cn.mycookies.pojo.dto.BlogAddRequest;
import cn.mycookies.pojo.dto.BlogListRequest;
import cn.mycookies.pojo.dto.BlogUpdateRequest;
import cn.mycookies.pojo.po.BlogDO;
import cn.mycookies.pojo.po.BlogDOExample;
import cn.mycookies.pojo.po.BlogTagsDO;
import cn.mycookies.pojo.po.BlogTagsDOExample;
import cn.mycookies.pojo.vo.BlogDetailVO;
import cn.mycookies.pojo.vo.BlogVO;
import cn.mycookies.pojo.vo.IndexVO;
import cn.mycookies.utils.DateCalUtils;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BlogService extends BaseService {

    @Autowired
    private BlogDOMapper blogDOMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogTagsDOMapper blogTagsDOMapper;

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
        if (blogDOMapper.insertSelective(blogDO) == 0) {
            return resultError4DB();
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
        BlogDO blogDO = blogDOMapper.selectByIdAndStatus(id, null);
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
        if (blogDOMapper.updateByPrimaryKey(blogDO) == 0) {
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
        BlogDOExample blogDOExample = new BlogDOExample();
        blogDOExample.createCriteria().andTitleEqualTo(newTitle);
        if (Objects.equals(newTitle, oldTitle) || CollectionUtils.isNotEmpty(blogDOMapper.selectByExample(blogDOExample))) {
            resultError4Param("标题[" + updateRequest.getTitle() + "]已存在");
        }
        blogDO.setTitle(newTitle);
        // 分类是否存在

        // 标签是否存在

        // code、status规整
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

        List<BlogDO> blogDOList = blogDOMapper.selectBlogs(queryRequest);

        List<BlogVO> blogVOList = blogDOList.stream().map(blogDO -> {
            BlogVO blogVO = convertBlogToVO(blogDO);
            // todo 博客的标签
//            blogVO.setTagList(tagService.getTagListOfBlog(blogDO.getId()));
            return blogVO;
        }).collect(Collectors.toList());

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(blogVOList);
        return ServerResponse.createBySuccess(pageInfo);
    }


    public ServerResponse<BlogVO> getBlog(Integer id, Byte isDeleted, boolean hasLastNext, boolean hasComments) {
        if (isDeleted == DataStatus.ALL) {
            isDeleted = null;
        }
        BlogDO blogDO = blogDOMapper.selectByPrimaryKey(id);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        BlogVO blogVO = convertBlogToVO(blogDO);
        // todo 博客的标签
//        blogVO.setTagList(tagService.getTagListOfBlog(id));
        // 查询last next
        if (hasLastNext) {
            blogVO.setLast(getlastOrNext(id, false));
            blogVO.setNext(getlastOrNext(id, true));
        }
        //查询 评论列表
        if (hasComments) {
            blogVO.setComments(commentService.listComments(1, 10, null, null, id, null, DataStatus.NO_DELETED).getData());
        }

        return ServerResponse.createBySuccess(blogVO);
    }

    public ServerResponse<BlogDO> getBlogById(Integer id) {

        BlogDO blogDO = blogDOMapper.selectByIdAndStatus(id, null);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess(blogDO);
    }

    public ServerResponse updateBlogCount(Integer id, String type) {
        BlogDO blogDO = blogDOMapper.selectByIdAndStatus(id, DataStatus.NO_DELETED);
        if (Objects.isNull(blogDO)) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAMAS_ERROR.inValue(), ActionStatus.PARAMAS_ERROR.getDescription());
        }
        int result = blogDOMapper.updateBlogCount(id, type);
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
        List<BlogVO> recommendList = blogDOMapper.selectHotOrRecommendBlogs(BlogStaticType.RECOMMEND_BLOG, 5);
        List<BlogVO> clickRankList = blogDOMapper.selectHotOrRecommendBlogs(BlogStaticType.HOT_BLOG, 5);
        // todo 标签分类信息
//        List<TagVO> tagVOS = tagService.listTagVOs(TagTypes.TAG_LABEL).getData();
//        List<TagVO> categoryVOS = tagService.listTagVOs(TagTypes.TAG_CATEGORY).getData();

        IndexVO indexVO = new IndexVO();
        if (withBlogs) {
            blogs = getBlogListInfos(null).getData();
            indexVO.setBlogList(blogs);
        }
//        indexVO.setCategoryList(categoryVOS);
//        indexVO.setTagList(tagVOS);
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

        BlogDO blogDO = blogDOMapper.selectByPrimaryKey(blogId);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("博客不存在");
        }
        // todo 获取博客的标签
//        List<Integer> tagIds = tagService.getTagListOfBlog(blogId).stream().map(TagVO::getId).collect(Collectors.toList());
//        return resultOk(BlogDetailVO.createFrom(blogDO, tagIds));
        return resultOk();
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
        BlogDO blogDO = blogDOMapper.selectLastOrNext(id, isLast);
        if (Objects.isNull(blogDO)) {
            return null;
        }
        return BlogDetailVO.createFrom(blogDO, null);
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

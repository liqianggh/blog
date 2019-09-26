package cn.mycookies.service;

import cn.mycookies.common.base.BaseService;
import cn.mycookies.common.base.ServerResponse;
import cn.mycookies.common.constants.*;
import cn.mycookies.common.exception.BusinessException;
import cn.mycookies.common.utils.DateCalUtils;
import cn.mycookies.common.utils.DateTimeUtil;
import cn.mycookies.common.utils.JsonUtil;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.dao.BlogTagsDOMapper;
import cn.mycookies.pojo.dto.*;
import cn.mycookies.pojo.meta.*;
import cn.mycookies.pojo.vo.BlogDetail4AdminVO;
import cn.mycookies.pojo.vo.BlogDetail4UserVO;
import cn.mycookies.pojo.vo.IndexVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 博客管理的service
 *
 * @author Jann Lee
 * @date 2019-08-18 11:00
 */
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

    @Value("${qiniu.url-prefix}")
    private String imgPrefix;

    /**
     * 添加新的博客
     *
     * @param addRequest 添加数据请求
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public ServerResponse<Boolean> createBlogInfo(BlogAddRequest addRequest) {
        BlogWithBLOBs blogDO = new BlogWithBLOBs();
        // 校验参数
        ServerResponse<Boolean> validateResult = validateAndInitCreateRequest(addRequest, blogDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        // 添加到数据库
        if (blogMapper.insertSelective(blogDO) == 0) {
            return resultError4DB();
        }

        List<Integer> tags = addRequest.getTags();
        if (CollectionUtils.isNotEmpty(tags)) {
            boolean bindResult = tagService.bindTags2Blog(blogDO.getId(), tags);
            if (!bindResult) {
                throw new BusinessException("添加博客标签绑定失败");
            }
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

        BlogWithBLOBs blogWithBLOBs = blogMapper.selectByIdAndStatus(id, null);
        ServerResponse<Boolean> validateResult = validateAndInitUpdateRequest(updateRequest, blogWithBLOBs);
        if (!validateResult.isOk()) {
            return validateResult;
        }

        // 删除旧的标签关联
        tagService.deleteTagsByBlogId(id);

        // 添加新的标签关联
        List<Integer> tagIds = updateRequest.getTags();
        for (Integer tagId : tagIds) {
            if (blogTagsDOMapper.insert(new BlogTagsDO(null, tagId, id)) == 0) {
                throw new BusinessException(ActionStatus.DATABASE_ERROR.inValue(), "添加标签过程失败");
            }
        }

        if (blogMapper.updateByPrimaryKeyWithBLOBs(blogWithBLOBs) == 0) {
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
    private ServerResponse<Boolean> validateAndInitCreateRequest(BlogAddRequest addRequest, BlogWithBLOBs blogDO) {
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
    private ServerResponse<Boolean> validateAndInitUpdateRequest(BlogUpdateRequest updateRequest, BlogWithBLOBs blogDO) {
        Preconditions.checkNotNull(updateRequest, "更新参数不能为null");
        Preconditions.checkNotNull(blogDO, "博客不存在");

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

        return resultOk();
    }

    /**
     * 分页查询博客
     *
     * @param queryRequest 查询条件
     * @return
     */
    public ServerResponse<PageInfo<BlogDetail4UserVO>> getBlogListInfos(BlogListRequest queryRequest) {
        Preconditions.checkNotNull(queryRequest, "查询参数不能为null");

        Page page = getPage(queryRequest);
        queryRequest.setStatus(DataStatus.NO_DELETED);
        List<BlogDO> blogDOList = blogMapper.selectBlogs(queryRequest);

        List<BlogDetail4UserVO> blogDetailList = blogDOList.stream().map(blogDO -> {
            BlogDetail4UserVO blogDetail = convertBlogDOToVO(blogDO);
            blogDetail.setTagList(tagService.getTagListByBlogId(blogDO.getId()));
            if (StringUtils.isNotBlank(blogDO.getImgUrl())) {
                blogDetail.setImgUrl(imgPrefix + blogDO.getImgUrl());
            }
            return blogDetail;
        }).collect(Collectors.toList());

        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(blogDetailList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * 获取博客详情
     *
     * @param id
     * @return
     */
    public ServerResponse<BlogDetail4UserVO> getBlogDetailsInfo(Integer id) {
        BlogWithBLOBs blogDO = blogMapper.selectByIdAndStatus(id, DataStatus.NO_DELETED);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("数据不存在");
        }

        BlogDetail4UserVO blogDetail4UserVO = convertBlogToVO(blogDO);
        // 博客的标签
        blogDetail4UserVO.setTagList(tagService.getTagListByBlogId(id));

        // 查询last next
        blogDetail4UserVO.setLast(getlastOrNext(id, false));
        blogDetail4UserVO.setNext(getlastOrNext(id, true));

        //查询 评论列表
        CommentListRequest commentListRequest = new CommentListRequest();
        commentListRequest.setTargetType(CommentTargetType.ARTICLE);
        blogDetail4UserVO.setComments(commentService.getCommentInfos(id, commentListRequest).getData());
        if (isValidViewOrLike(id.toString())) {
            updateBlogCount(id, BlogStaticType.VIEW);
        }

        return ServerResponse.createBySuccess(blogDetail4UserVO);
    }

    /**
     * 根据id获取博客
     *
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

        if (!isValidViewOrLike(type + "_blog_" + id)) {
            return resultError();
        }

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
     * 获取首页所需数据
     *
     * @param withBlogs 结果集是否包含博客列表
     * @return
     */
    public ServerResponse<IndexVO> getIndexVO(boolean withBlogs) {
        //查询推荐或热门博客
        List<BlogDetail4UserVO> recommendList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.RECOMMEND_BLOG, 5);
        List<BlogDetail4UserVO> clickRankList = blogMapper.selectHotOrRecommendBlogs(BlogStaticType.HOT_BLOG, 5);

        // 标签分类信息
        List<TagWithCountVO> tagVOS = tagService.getTagListByTagType(TagType.TAG);
        List<TagWithCountVO> categoryVOS = tagService.getTagListByTagType(TagType.CATEGORY);

        // 博客内容
        IndexVO indexVO = new IndexVO();
        if (withBlogs) {
            BlogListRequest blogListRequest = new BlogListRequest();
            blogListRequest.setStatus(DataStatus.NO_DELETED);
            PageInfo<BlogDetail4UserVO> blogs = getBlogListInfos(blogListRequest).getData();
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
    public ServerResponse<BlogDetail4AdminVO> getBlogDetailInfo(Integer blogId) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");

        BlogWithBLOBs blogDO = blogMapper.selectByPrimaryKey(blogId);
        if (Objects.isNull(blogDO)) {
            return resultError4Param("博客不存在");
        }
        // 获取博客的标签
        List<Integer> tagIds = tagService.getTagListByBlogId(blogId)
                .stream()
                .map(TagVO::getId)
                .collect(Collectors.toList());

        return resultOk(BlogDetail4AdminVO.createFrom(blogDO, tagIds));
    }

    /**
     * 获取博客的上一篇或者下一篇
     *
     * @param id     当前博客主键id
     * @param isLast 是否是上一篇
     * @return
     */
    private BlogDetail4AdminVO getlastOrNext(Integer id, boolean isLast) {
        Preconditions.checkNotNull(id, "主键id不能为null");

        BlogDO blogDO = blogMapper.selectLastOrNext(id, isLast);
        if (Objects.isNull(blogDO)) {
            return null;
        }

        BlogDetail4AdminVO blogDetail = new BlogDetail4AdminVO();
        blogDetail.setId(blogDO.getId());
        blogDetail.setTitle(blogDO.getTitle());
        blogDetail.setCategoryId(blogDO.getCategoryId());
        return blogDetail;
    }

    private BlogDetail4UserVO convertBlogToVO(BlogWithBLOBs blogDO) {

        BlogDetail4UserVO blogDetail4UserVo = convertBlogDOToVO(blogDO);
        blogDetail4UserVo.setHtmlContent(blogDO.getHtmlContent());
        List<CatalogItem> catalogItems = JsonUtil.stringToObj(blogDO.getBlogCatalog(), List.class, CatalogItem.class);
        blogDetail4UserVo.setCatalogs(catalogItems);

        return blogDetail4UserVo;
    }

    private BlogDetail4UserVO convertBlogDOToVO(BlogDO blogDO) {
        BlogDetail4UserVO blogDetail4UserVo = new BlogDetail4UserVO();
        blogDetail4UserVo.setCalcTime(DateCalUtils.format(new Date(blogDO.getCreateTime())));
        blogDetail4UserVo.setCreateTime(DateTimeUtil.dateToStr(blogDO.getCreateTime()));
        blogDetail4UserVo.setUpdateTime(DateTimeUtil.dateToStr(blogDO.getUpdateTime()));
        blogDetail4UserVo.setId(blogDO.getId());
        blogDetail4UserVo.setSummary(blogDO.getSummary());
        blogDetail4UserVo.setImgUrl(blogDO.getImgUrl());
        blogDetail4UserVo.setViewCount(blogDO.getViewCount());
        blogDetail4UserVo.setLikeCount(blogDO.getLikeCount());
        blogDetail4UserVo.setTitle(blogDO.getTitle());
        blogDetail4UserVo.setCode(blogDO.getCode());
        blogDetail4UserVo.setCategoryId(blogDO.getCategoryId());

        return blogDetail4UserVo;
    }

    /**
     * 逻辑删除博客
     *
     * @param id 博客主键id
     * @return
     */
    public ServerResponse<Boolean> deleteBlogInfo(Integer id) {
        Preconditions.checkNotNull(id, "要删除的博客id不能未空");

        BlogDO blogDO = getBlogById(id);
        if (Objects.isNull(blogDO)) {
            return resultOk();
        }

        fillUpdateTime(blogDO);
        blogDO.setBlogStatus(DataStatus.DELETED);
        if (blogMapper.updateByPrimaryKey(blogDO) == 0) {
            return resultError4DB("删除失败");
        }

        return resultOk();
    }
}

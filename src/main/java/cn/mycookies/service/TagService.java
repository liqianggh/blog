package cn.mycookies.service;

import cn.mycookies.common.KeyValueVO;
import cn.mycookies.common.base.BaseService;
import cn.mycookies.common.base.ServerResponse;
import cn.mycookies.common.constants.TagType;
import cn.mycookies.dao.BlogMapper;
import cn.mycookies.dao.BlogTagsDOMapper;
import cn.mycookies.dao.TagMapper;
import cn.mycookies.pojo.dto.*;
import cn.mycookies.pojo.meta.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 标签service
 *
 * @author Jann Lee
 * @date 2019-07-03 23:42
 */
@Service
public class TagService extends BaseService {

    @Resource
    private TagMapper tagMapper;

    @Resource
    private BlogTagsDOMapper blogTagsDOMapper;

    @Resource
    private BlogMapper blogMapper;

    /**
     * 获取标签列表信息
     *
     * @param tagListRequest 标签查询请求
     * @return
     */
    public ServerResponse<PageInfo<TagVO>> getTagListInfos(TagListRequest tagListRequest) {
        Preconditions.checkNotNull(tagListRequest, "请求参数不能为null");

        Page page = getPage(tagListRequest);
        TagDOExample example = new TagDOExample();
        if (Objects.nonNull(tagListRequest.getTagType())) {
            example.createCriteria().andTagTypeEqualTo(tagListRequest.getTagType());
        }
        List<TagDO> tagDOList = tagMapper.selectByExample(example);
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(tagDOList.stream().map(TagVO::createFrom).collect(Collectors.toList()));

        return resultOk(pageInfo);
    }

    /**
     * 获取tagVo列表
     *
     * @param tagType 类型
     * @return
     */
    public ServerResponse<List<TagVO>> getAllTagListInfsByType(Integer tagType) {

        return resultOk(getAllTagsByType(tagType).stream().map(TagVO::createFrom).collect(Collectors.toList()));
    }

    /**
     * 获取所有的k-v结构标签信息
     *
     * @param tagType
     * @return
     */
    public ServerResponse<List<KeyValueVO<Integer, String>>> getAllTagList(Integer tagType) {

        return resultOk(getAllTagsByType(tagType).stream().map(tagDO -> new KeyValueVO<Integer, String>(tagDO.getId(), tagDO.getTagName())).collect(Collectors.toList()));
    }

    /**
     * 获取所有的标签，根据id
     *
     * @param tagType
     * @return
     */
    private List<TagDO> getAllTagsByType(Integer tagType) {
        TagDOExample tagDOExample = new TagDOExample();
        tagDOExample.createCriteria().andTagTypeEqualTo(tagType);

        return tagMapper.selectByExample(tagDOExample);
    }

    /**
     * 添加标签
     *
     * @param tagAddRequest 请求参数
     * @return
     */
    public ServerResponse<Boolean> createTagInfo(TagAddRequest tagAddRequest) {
        TagDO tagDO = new TagDO();
        ServerResponse<Boolean> validateResult = validateAndInitCreateRequest(tagAddRequest, tagDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }

        if (tagMapper.insert(tagDO) == 0) {
            return resultError4DB("添加标签失败");
        }
        return resultOk();
    }


    /**
     * todo 验证添加参数
     *
     * @param tagAddRequest 请求参数
     * @param tagDO
     * @return
     */
    private ServerResponse<Boolean> validateAndInitCreateRequest(TagAddRequest tagAddRequest, TagDO tagDO) {
        Preconditions.checkNotNull(tagDO, "要添加的参数不能为null");
        fillCreateTime(tagDO);

        return validateAndInitUpdateRequest(tagAddRequest, tagDO);
    }

    /**
     * 更新标签
     *
     * @param id            主键id
     * @param updateRequest 请求参数
     * @return
     */
    public ServerResponse<Boolean> updateTagInfo(Integer id, TagUpdateRequest updateRequest) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        TagDO tagDO = tagMapper.selectByPrimaryKey(id);
        ServerResponse<Boolean> validateResult = validateAndInitUpdateRequest(updateRequest, tagDO);
        if (!validateResult.isOk()) {
            return validateResult;
        }
        if (tagMapper.updateByPrimaryKey(tagDO) == 0) {
            return resultError4DB("更新失败");
        }
        return resultOk();
    }

    /**
     * 逻辑校验
     *
     * @param updateRequest
     * @param tagDO
     * @return
     */
    public ServerResponse<Boolean> validateAndInitUpdateRequest(TagUpdateRequest updateRequest, TagDO tagDO) {
        Preconditions.checkNotNull(updateRequest, "更新参数不能为null");
        if (Objects.isNull(tagDO)) {
            return resultError4Param("要修改的数据不存在");
        }
        // 是否重名
        TagDOExample tagDOExample = new TagDOExample();
        tagDOExample.createCriteria().andTagNameEqualTo(updateRequest.getTagName());
        List<TagDO> tagDOList = tagMapper.selectByExample(tagDOExample);
        if (CollectionUtils.isNotEmpty(tagDOList)) {
            for (TagDO tempDO : tagDOList) {
                if (Objects.equals(tempDO.getTagType(), tagDO.getTagType())) {
                    return resultError4Param("名称为[" + tagDO.getTagName() + "]的标签已存在");
                }
            }
        }
        tagDO.setTagName(updateRequest.getTagName());
        tagDO.setTagDesc(updateRequest.getTagDesc());
        tagDO.setTagType(updateRequest.getTagType());
        fillUpdateTime(tagDO);

        return resultOk();
    }

    /**
     * 根据id获取详情
     *
     * @param id 主键id
     * @return
     */
    public ServerResponse<TagVO> getTagDetailInfoById(Integer id) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        TagDO tagDO = tagMapper.selectByPrimaryKey(id);
        if (Objects.isNull(tagDO)) {
            return resultError4Param("该标签不存在");
        }

        return resultOk(TagVO.createFrom(tagMapper.selectByPrimaryKey(id)));
    }

    public List<TagDO> getTagListByTypeAndIds(TagType tagType, List<Integer> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Lists.newArrayList();
        }
        TagDOExample tagDOExample = new TagDOExample();
        tagDOExample.createCriteria().andTagTypeEqualTo(tagType.getCode()).andIdIn(ids);
        if (Objects.nonNull(tagType)) {
            tagDOExample.createCriteria().andTagTypeEqualTo(tagType.getCode());
        }

        return tagMapper.selectByExample(tagDOExample);
    }

    /**
     * 根据主键id删除标签
     *
     * @param id 主键id
     * @return
     */
    public ServerResponse<TagVO> deleteTagInfoById(Integer id) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        BlogTagsDOExample example = new BlogTagsDOExample();
        example.createCriteria().andTagIdEqualTo(id);
        BlogExample blogExample = new BlogExample();
        blogExample.createCriteria().andCategoryIdEqualTo(id);

        if (CollectionUtils.isNotEmpty(blogTagsDOMapper.selectByExample(example))
                || CollectionUtils.isNotEmpty((blogTagsDOMapper.selectByExample(example)))) {
            return resultError4Param("改标签正在被使用");
        }
        TagDO targetDO = tagMapper.selectByPrimaryKey(id);
        if (Objects.isNull(targetDO)) {
            return resultOk();
        }
        if (tagMapper.deleteByPrimaryKey(id) == 0) {
            return resultError4DB("删除失败");
        }
        return resultOk();
    }


    public List<TagVO> getTagListByBlogId(Integer blogId) {
        if (Objects.isNull(blogId)) {
            return Lists.newArrayList();
        }

        return tagMapper.selectTagsOfBlog(blogId)
                .stream()
                .filter(Objects::nonNull)
                .map(TagVO::createFrom)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取标签
     *
     * @param tagType   标签类型
     * @return
     */
    public List<TagWithCountVO> getTagListByTagType(TagType tagType) {
        Preconditions.checkNotNull(tagType, "标签类型不能为null");
        if (Objects.equals(tagType, TagType.CATEGORY)) {
            return tagMapper.selectCategoryList();
        } else {
            return tagMapper.selectTagList();
        }
    }


    /**
     * 删除博客关联的标签
     *
     * @param blogId
     */
    public boolean deleteTagsByBlogId(Integer blogId) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");

        BlogTagsDOExample example = new BlogTagsDOExample();
        example.createCriteria().andBlogIdEqualTo(blogId);
        return blogTagsDOMapper.deleteByExample(example) > 0;
    }

    /**
     * 给博客绑定标签
     *
     * @param blogId    博客id
     * @param tagIds    标签集合
     * @return
     */
    public boolean bindTags2Blog(Integer blogId, List<Integer> tagIds) {
        Preconditions.checkNotNull(blogId, "博客id不能为null");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(tagIds), "标签id列表不能为空");

        AtomicInteger count = new AtomicInteger();
        tagIds.stream().forEach(tagId -> {
            count.addAndGet(blogTagsDOMapper.insert(new BlogTagsDO(null, tagId, blogId)));
        });

        return count.intValue() == tagIds.size();
    }

}

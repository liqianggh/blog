package cn.mycookies.service;

import cn.mycookies.common.BaseService;
import cn.mycookies.common.KeyValueVO;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagType;
import cn.mycookies.dao.TagMapper;
import cn.mycookies.pojo.dto.*;
import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.po.TagDOExample;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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

    /**
     * 获取标签列表信息
     *
     * @param tagListRequest 标签查询请求
     * @return
     */
    public ServerResponse<PageInfo<TagVO>> getTagListInfos(TagListRequest tagListRequest) {
        Page page = getPage(tagListRequest);
        TagDOExample example = new TagDOExample();
        example.createCriteria().andTagTypeEqualTo(tagListRequest.getTagType());
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

        return resultOk(getAllTagsByType(tagType)
                .stream()
                .map(tagDO -> new KeyValueVO<Integer, String>(tagDO.getId(), tagDO.getTagName()))
                .collect(Collectors.toList()));
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
     *
     * todo 验证添加参数
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
     * todo 校验逻辑
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
        // 更新操作
        if (Objects.isNull(tagDO)) {

        }
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

    /**
     * 根据主键id删除标签
     *
     * @param id 主键id
     * @return
     */
    public ServerResponse<TagVO> deleteTagInfoById(Integer id) {
        Preconditions.checkNotNull(id, "id信息不能为null");
        // todo 判断是否被绑定，如果没有被绑定则可以删除
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
        return  tagMapper.selectTagsOfBlog(blogId).stream()
                .map(TagVO::createFrom)
                .collect(Collectors.toList());
    }

    public List<TagWithCountVO> getTagListByTagType(TagType tagType) {
        Preconditions.checkNotNull(tagType, "标签类型不能为null");
        if (Objects.equals(tagType, TagType.CATEGORY)) {
            return tagMapper.selectCategoryList();
        } else {
            return tagMapper.selectTagList();
        }
    }
}

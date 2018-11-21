package cn.mycookies.service.impl;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.DataStatus;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagTypes;
import cn.mycookies.dao.TagMapper;
import cn.mycookies.pojo.dto.TagAdd;
import cn.mycookies.pojo.dto.TagBo;
import cn.mycookies.pojo.po.Tag;
import cn.mycookies.pojo.vo.TagVO;
import cn.mycookies.service.TagService;
import cn.mycookies.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName TagServiceImpl
 * @Description 实现类
 * @Author Jann Lee
 * @Date 2018-11-18 17:02
 **/
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public PageInfo<TagBo> findTagList(int pageNum, int pageSize,Byte type) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_time desc");
        Tag param = new Tag();
        param.setType(type);
        List<Tag> tagList = tagMapper.queryTagList(param);

        List<TagBo> list = new ArrayList<>();
        tagList.stream().forEach(tag -> {

            TagBo tagBo = convertTagToBo(tag);
            list.add(tagBo);
        });
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 获取tagVo列表
     * @param type
     * @return
     */
    @Override
    public ServerResponse<List<TagVO>> findTagVoList(Byte type) {

        List<TagVO> tagList = null;
        if (type == TagTypes.TAG_LABEL) {
            tagList = tagMapper.queryTagBoList();
        } else {
            tagList = tagMapper.queryTagVOList();
        }

        if (tagList==null || tagList.size() ==0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(),ActionStatus.NO_RESULT.getDescription());
        } else {
            return ServerResponse.createBySuccess(tagList);
        }
     }
    private TagBo convertTagToBo(Tag tag) {
        if (Objects.isNull(tag)) {
            return null;
        }
        TagBo tagBo = new TagBo();
        tagBo.setId(tag.getId());
        tagBo.setTagName(tag.getTagName());
        tagBo.setTagDesc(tag.getTagDesc());
        tagBo.setType(tag.getType());
        tagBo.setCreateTime(DateTimeUtil.dateToStr(tag.getCreateTime()));
        tagBo.setUpdateTime(DateTimeUtil.dateToStr(tag.getUpdateTime()));
        return tagBo;
    }

    @Override
    public List<TagBo> findAllTagsWithCount() {
        return null;
    }

    @Override
    public ServerResponse<Boolean> addTag(TagAdd tagAdd) {
        if (tagAdd == null || tagAdd.getTagName() == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        Tag param = new Tag();
        param.setTagName(tagAdd.getTagName());
        param.setType(tagAdd.getType());
        // 是否存在校验
        Tag tag = tagMapper.queryByName(param);
        if (tag != null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATA_REPEAT.inValue(), ActionStatus.DATA_REPEAT.getDescription());
        }
        Integer result = tagMapper.insert(tagAdd);
        if (result == 0) {
            return ServerResponse.createByError();
        } else {
            return ServerResponse.createBySuccess();
        }
    }

    @Override
    public ServerResponse updateTag(Tag tag) {

        if (tag == null || tag.getTagName() == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        // 是否存在校验
        Tag tagResult = tagMapper.queryByName(tag);
        if (tagResult != null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATA_REPEAT.inValue(), ActionStatus.DATA_REPEAT.getDescription());
        }

        tagResult = tagMapper.queryById(tag);
        if (tagResult == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }

        Integer result = tagMapper.updateTag(tag);
        if (result > 0) {
            return ServerResponse.createBySuccess(true);
        } else {
            return ServerResponse.createByError();
        }
    }

    @Override
    public ServerResponse<TagBo> selectTagById(Integer id,Byte type) {

        if(id == 0 ){
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(),ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        Tag param = new Tag();
        param.setId(id);
        param.setType(type);
        Tag tagResult = tagMapper.queryById(param);
        if (tagResult == null || StringUtils.isEmpty(tagResult.getTagName())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess(convertTagToBo(tagResult));
    }

    @Override
    public ServerResponse<TagBo> deleteById(Integer id,Byte type,Byte status) {
        Tag param = new Tag();
        param.setId(id);
        param.setType(type);
        if (status == DataStatus.NO_DELETED) {

        }
        Tag tagResult = tagMapper.queryById(param);
        if (tagResult == null || StringUtils.isEmpty(tagResult.getTagName())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        int result = tagMapper.deleteById(param);
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(),ActionStatus.DATABASE_ERROR.getDescription());
        }
    }


}

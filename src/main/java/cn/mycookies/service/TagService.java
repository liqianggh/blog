package cn.mycookies.service;

import cn.mycookies.common.ActionStatus;
import cn.mycookies.common.KeyValueVO;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.common.TagTypes;
import cn.mycookies.dao.TagDOMapper;
import cn.mycookies.pojo.dto.TagAddDTO;
import cn.mycookies.pojo.dto.TagDTO;
import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.vo.TagVO;
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
import java.util.stream.Collectors;

@Service
public class TagService {

    @Autowired
    private TagDOMapper tagDOMapper;


    public PageInfo<TagDTO> listTags(int pageNum, int pageSize, Byte type) {
        Page page = PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("create_time desc");
        TagDO param = new TagDO();
        param.setType(type);
        List<TagDO> tagDOList = tagDOMapper.queryTagList(param);

        List<TagDTO> list = new ArrayList<>();
        tagDOList.stream().forEach(tagDO -> {

            TagDTO tagDTO = convertTagToBo(tagDO);
            list.add(tagDTO);
        });
        PageInfo pageInfo = page.toPageInfo();
        pageInfo.setList(list);
        return pageInfo;
    }

    /**
     * 获取tagVo列表
     *
     * @param type
     * @return
     */

    public ServerResponse<List<TagVO>> listTagVOs(Byte type) {

        List<TagVO> tagList = null;
        if (type == TagTypes.TAG_LABEL) {
            tagList = tagDOMapper.queryTagBoList();
        } else {
            tagList = tagDOMapper.queryCategoryVOList();
        }

        if (tagList == null || tagList.size() == 0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        } else {
            return ServerResponse.createBySuccess(tagList);
        }
    }


    public List<TagVO> listTagsOfBlog(Integer blogId) {

        List<TagVO> tagVOS = tagDOMapper.queryTagsOfBlog(blogId);

        return tagVOS;
    }


    public List<KeyValueVO<Integer, String>> getAllTagList(byte tagCategory) {
        TagDO param = new TagDO();
        param.setType(tagCategory);
        List<TagDO> tagDOS = tagDOMapper.queryTagList(param);
        return tagDOS.stream().map(tagDO -> new KeyValueVO<Integer, String>(tagDO.getId(), tagDO.getTagName())).collect(Collectors.toList());
    }

    private TagDTO convertTagToBo(TagDO tagDO) {
        if (Objects.isNull(tagDO)) {
            return null;
        }
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tagDO.getId());
        tagDTO.setTagName(tagDO.getTagName());
        tagDTO.setTagDesc(tagDO.getTagDesc());
        tagDTO.setType(tagDO.getType());
        tagDTO.setCreateTime(DateTimeUtil.dateToStr(tagDO.getCreateTime()));
        tagDTO.setUpdateTime(DateTimeUtil.dateToStr(tagDO.getUpdateTime()));
        return tagDTO;
    }


    public ServerResponse<Boolean> insertTag(TagAddDTO tagAddDTO) {
        if (tagAddDTO == null || tagAddDTO.getTagName() == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        TagDO param = new TagDO();
        param.setTagName(tagAddDTO.getTagName());
        param.setType(tagAddDTO.getType());
        // 是否存在校验
        TagDO tagDO = tagDOMapper.queryByName(param);
        if (tagDO != null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATA_REPEAT.inValue(), ActionStatus.DATA_REPEAT.getDescription());
        }
        Integer result = tagDOMapper.insert(null);
        if (result == 0) {
            return ServerResponse.createByError();
        } else {
            return ServerResponse.createBySuccess();
        }
    }


    public ServerResponse updateTag(TagDO tagDO) {

        if (tagDO == null || tagDO.getTagName() == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        // 是否存在校验
        TagDO tagDOResult = tagDOMapper.queryByName(tagDO);
        if (tagDOResult != null && Objects.equals(tagDO.getId(), tagDOResult.getId()) && StringUtils.equals(tagDO.getTagDesc(), tagDOResult.getTagDesc())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATA_REPEAT.inValue(), ActionStatus.DATA_REPEAT.getDescription());
        }

        tagDOResult = tagDOMapper.queryById(tagDO);
        if (tagDOResult == null) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }

        Integer result = tagDOMapper.updateTag(tagDO);
        if (result > 0) {
            return ServerResponse.createBySuccess(true);
        } else {
            return ServerResponse.createByError();
        }
    }


    public ServerResponse<TagDTO> getTagById(Integer id, Byte type) {

        if (id == 0) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        TagDO param = new TagDO();
        param.setId(id);
        param.setType(type);
        TagDO tagDOResult = tagDOMapper.queryById(param);
        if (tagDOResult == null || StringUtils.isEmpty(tagDOResult.getTagName())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.NO_RESULT.inValue(), ActionStatus.NO_RESULT.getDescription());
        }
        return ServerResponse.createBySuccess(convertTagToBo(tagDOResult));
    }


    public ServerResponse<TagDTO> deleteById(Integer id, Byte type) {
        TagDO param = new TagDO();
        param.setId(id);
        param.setType(type);
        // todo 判断是否被绑定，如果没有被绑定则可以删除
        TagDO tagDOResult = tagDOMapper.queryById(param);
        if (tagDOResult == null || StringUtils.isEmpty(tagDOResult.getTagName())) {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.PARAM_ERROR_WITH_ERR_DATA.inValue(), ActionStatus.PARAM_ERROR_WITH_ERR_DATA.getDescription());
        }
        int result = tagDOMapper.deleteById(param);
        if (result > 0) {
            return ServerResponse.createBySuccess();
        } else {
            return ServerResponse.createByErrorCodeMessage(ActionStatus.DATABASE_ERROR.inValue(), ActionStatus.DATABASE_ERROR.getDescription());
        }
    }


}

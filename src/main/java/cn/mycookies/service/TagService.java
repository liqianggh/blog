package cn.mycookies.service;

import cn.mycookies.common.KeyValueVO;
import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.TagAddDTO;
import cn.mycookies.pojo.dto.TagDTO;
import cn.mycookies.pojo.po.TagDO;
import cn.mycookies.pojo.vo.TagVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @className TagService
 * @description 标签管理相关接口
 * @author Jann Lee
 * @date 2018-11-18 17:01
 **/
public interface TagService {

    /**
     * 分页查询标签
     * @param pageSize 每页数据条数
     * @param pageNum  当前页码数
     * @return
     */
     PageInfo<TagDTO> listTags(int pageNum, int pageSize, Byte type);


    /**
     * 添加标签
     * @param tagAddDTO
     * @return true/false
     */
    ServerResponse<Boolean> insertTag(TagAddDTO tagAddDTO);

    /**
     * 修改标签信息
     * @param tagDO
     * @return
     */
    ServerResponse updateTag(TagDO tagDO);

    /**
     * 公国主键id查询标签
     * @param id
     * @param type
     * @return
     */
    ServerResponse<TagDTO> getTagById(Integer id, Byte type);

    /**
     * 删除
     * @param id 主键
     * @param type 是分类还是标签
      * @return
     */
    ServerResponse<TagDTO> deleteById(Integer id, Byte type);

    /**
     * 获取标签类表
     * @param type 分类化石标签
     * @return
     */
    ServerResponse<List<TagVO>> listTagVOs(Byte type);

    /**
     * 查询博客的标签
     * @param blogId 博客的id
     * @return
     */
    List<TagVO> listTagsOfBlog(Integer blogId);

    List<KeyValueVO<Integer, String>> getAllTagList(byte tagCategory);
}

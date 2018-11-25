package cn.mycookies.service;

import cn.mycookies.common.ServerResponse;
import cn.mycookies.pojo.dto.TagAdd;
import cn.mycookies.pojo.dto.TagBo;
import cn.mycookies.pojo.po.Tag;
import cn.mycookies.pojo.vo.TagVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @ClassName TagService
 * @Description 标签管理相关接口
 * @Author Jann Lee
 * @Date 2018-11-18 17:01
 **/
public interface TagService {

    /**
     * 分页查询标签
     * @param pageSize 每页数据条数
     * @param pageNum  当前页码数
     * @return
     */
     PageInfo<TagBo> findTagList(int pageNum, int pageSize,Byte type);


    /**
     * 添加标签
     * @param tagAdd
     * @return true/false
     */
    ServerResponse<Boolean> addTag(TagAdd tagAdd);

    /**
     * 修改标签信息
     * @param tag
     * @return
     */
    ServerResponse updateTag(Tag tag);

    ServerResponse<TagBo> selectTagById(Integer id,Byte type);

    ServerResponse<TagBo> deleteById(Integer id,Byte type,Byte isDelete);

    ServerResponse<List<TagVO>> findTagVoList(Byte type);

    List<TagVO> geteTagsOfBlog(Integer blogId);
}

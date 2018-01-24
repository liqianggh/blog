package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Tag;
import cn.blog.vo.TagVo;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ITagService {

    ServerResponse saveOrUpdate(Tag tag);

    ServerResponse delete(Integer tagId);

    ServerResponse<PageInfo> listAllSimple(Integer pageNum,Integer pageSize);
    List<TagVo> listAllSimple();



    ServerResponse<PageInfo> listAll(Integer pageNum, Integer pageSize);
    List<Tag> listAll();


}

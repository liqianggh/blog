package cn.blog.service;

import cn.blog.common.ServerResponse;
import cn.blog.pojo.Tag;

import java.util.List;

public interface ITagService {

    ServerResponse saveOrUpdate(Tag tag);

    ServerResponse delete(Integer tagId);

    ServerResponse<List<Tag>> listAllSimple();

    ServerResponse<List<Tag>> listAll();


}

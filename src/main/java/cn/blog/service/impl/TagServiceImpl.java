package cn.blog.service.impl;

import cn.blog.common.ResponseCode;
import cn.blog.common.ServerResponse;
import cn.blog.dao.TagMapper;
import cn.blog.pojo.Tag;
import cn.blog.service.ITagService;
import cn.blog.util.DateCalUtils;
import cn.blog.util.DateTimeUtil;
import cn.blog.vo.TagVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static cn.blog.common.ServerResponse.createBySuccess;

/**
  * @Description: 博客标签的业务逻辑
  * Created by Jann Lee on 2018/1/20  14:37.
  */
@Service("iTagService")
public class TagServiceImpl implements ITagService{


    @Autowired
    private TagMapper tagMapper;
    //todo  更新时日期转换
    @Override
    public ServerResponse saveOrUpdate(Tag tag) {
        //校验数据是否为空
        if(tag==null){
            return  ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        if(StringUtils.isEmpty(tag.getTagName())){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int rowCount=0;
        if(tag.getTagId()==null){
            //新增
            rowCount = tagMapper.insertSelective(tag);

        }else{
            //更新
            rowCount=tagMapper.updateByPrimaryKeySelective(tag);
        }
        if(rowCount>0){
            return createBySuccess();
        }
        return ServerResponse.createByErrorMessage("修改/新增失败！");
    }


    @Override
    public ServerResponse delete(Integer tagId) {
        if(tagId==null){
            return  ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }
        //存在性检验
        Tag result = tagMapper.selectByPrimaryKey(tagId);
        if(result==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        int rowCount = tagMapper.deleteByPrimaryKey(tagId);

        if(rowCount>0){
            return createBySuccess();
        }
        return ServerResponse.createByErrorMessage("删除失败！");
    }


     @Override
     public ServerResponse<PageInfo> listAllSimple(Integer pageNum,Integer pageSize) {
         PageHelper.startPage(pageNum,pageSize);
        List<TagVo> tagVoList = tagMapper.findAllSimple();
        //todo
        PageInfo  pageInfo = new PageInfo (tagVoList);
        return ServerResponse.createBySuccess(pageInfo);
     }

     @Override
     public ServerResponse<PageInfo> listAll(Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
         List<Tag> tagList = tagMapper.findALl();

         PageInfo  pageInfo = new PageInfo (tagList);
         List<TagVo> tagVOList = Lists.newArrayList();
         for(Tag tagItem:tagList){
             TagVo tagVo = new TagVo();
             BeanUtils.copyProperties(tagItem,tagVo);
             tagVo.setCreateTimeStr(DateTimeUtil.dateToStr(tagItem.getCreateTime()));
             tagVo.setUpdateTimeStr(DateTimeUtil.dateToStr(tagItem.getUpdateTime()));

             tagVOList.add(tagVo);
         }
         pageInfo.setList(tagVOList);
         return ServerResponse.createBySuccess(pageInfo);
     }

}

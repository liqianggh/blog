package cn.blog.service.impl;

import cn.blog.common.ResponseCode;
import cn.blog.common.ServerResponse;
import cn.blog.dao.BlogMapper;
import cn.blog.pojo.Blog;
import cn.blog.service.IBlogService;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
  * @Description: 博客模块业务逻辑处理
  * Created by Jann Lee on 2018/1/20  0:51.
  */
@Slf4j
@Service("iBlogService")
public class BlogServiceImpl implements IBlogService {

    @Autowired
    private BlogMapper blogMapper;

     /**
       * @Description:添加或修改（高复用）
       * Created by Jann Lee on 2018/1/18  18:54.
       */
    @Override
    public ServerResponse saveOrUpdate(Blog blog) {
        int rowCount = 0;
        if(blog==null){
            return ServerResponse.createByErrorCodeAndMessage(ResponseCode.NULL_ARGUMENT.getCode(),ResponseCode.NULL_ARGUMENT.getDesc());
        }else if(blog.getBlogId()!=null){
            //如果有id就更新
            //todo 校验管理员是否登录
            rowCount = blogMapper.updateByPrimaryKeySelective(blog);
        }else{
            //没有id就添加
            //校验参数
            if(StringUtils.isNullOrEmpty(blog.getTitle())||StringUtils.isNullOrEmpty(blog.getContent())){
                return ServerResponse.createByErrorCodeAndMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
            }
            log.info(blog.toString());
            blog.setCategoryId(1);
            rowCount=blogMapper.insertSelective(blog);
        }
        if(rowCount>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("修改/新增失败！");
    }
}

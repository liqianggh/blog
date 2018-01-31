package cn.blog.service.CacheService;

import cn.blog.bo.RemoteUser;
import cn.blog.common.Const;
import cn.blog.dao.CategoryMapper;
import cn.blog.dao.TagMapper;
import cn.blog.pojo.Category;
import cn.blog.util.JsonUtil;
import cn.blog.util.RedisPoolUtil;
import cn.blog.vo.CategoryVo;
import cn.blog.vo.TagVo;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CacheService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    public void initCache() {
        //标签
        //查询出来数据
        List<TagVo> tagVoList = tagMapper.findALlWithCount();
        //转换成字符串
        String tagVoListStr = JsonUtil.objToString(tagVoList);
        //存入jedis
        RedisPoolUtil.set(Const.CacheTypeName.TAGS_WITHCOUNT, tagVoListStr);


        //分类
        List<CategoryVo> categoryList = categoryMapper.selectAllWithBlogCount();
        String categoryVoStr = JsonUtil.objToString(categoryList);
        RedisPoolUtil.set(Const.CacheTypeName.CATEGORY_WITHCOUNT, categoryVoStr);

    }

    public List<TagVo> findTagsWithCount() {
        String tagsVoListJson = RedisPoolUtil.get(Const.CacheTypeName.TAGS_WITHCOUNT);
        List<TagVo> tagVoList = JsonUtil.stringToObj(tagsVoListJson, List.class, TagVo.class);
        return tagVoList;
    }

    public List<CategoryVo> findAllCategoryWithBlogCount() {
        String catetoryVoListJson = RedisPoolUtil.get(Const.CacheTypeName.CATEGORY_WITHCOUNT);
        List<CategoryVo> categoryVoList = JsonUtil.stringToObj(catetoryVoListJson, List.class, CategoryVo.class);
        return categoryVoList;
    }


    public Boolean  isLikeUserExists(String remoteUserAddr) {
        Boolean result = RedisPoolUtil.hexists(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS,remoteUserAddr);
        return result;
    }

    public Long  addLikeUser(String remoteUserAddr) {
        //todo
        Long result = RedisPoolUtil.hset(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS,remoteUserAddr,Boolean.toString(true),Const.CacheTime.ADD_LIKE_TIME);

        return result;
    }
    public Boolean  getUserLikeStatus(String remoteUserAddr) {
        Boolean result = JsonUtil.stringToObj(RedisPoolUtil.hget(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS,remoteUserAddr),Boolean.class);
        if(result==null||result==false){
            return false;
        }
        return result;
    }



    public void addBlogViewer(String remoteUserAddr, Integer blogId){
        RedisPoolUtil.sadd(Const.CacheTypeName.REMOTE_VIEW_USER+"_"+remoteUserAddr,blogId.toString(),Const.CacheTime.VIEW_COUNT_TIME);
    }

    public boolean isBlogViewerExists(String remoteUserAddr, Integer blogId) {
        return RedisPoolUtil.sismember(Const.CacheTypeName.REMOTE_VIEW_USER + "_" + remoteUserAddr, blogId.toString());
    }
}

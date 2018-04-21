package cn.blog.service.CacheService;

import cn.blog.common.Const;
import cn.blog.dao.CategoryMapper;
import cn.blog.dao.TagMapper;
import cn.blog.dao.VisitorMapper;
import cn.blog.pojo.Visitor;
import cn.blog.util.JsonUtil;
import cn.blog.util.PropertiesUtil;
import cn.blog.util.RedisShardedPoolUtil;
import cn.blog.vo.CategoryVo;
import cn.blog.vo.TagVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class CacheService {

    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private VisitorMapper visitorMapper;

    public void initCache() {

        //标签
        //查询出来数据
        List<TagVo> tagVoList = tagMapper.findALlWithCount();
        //转换成字符串
        String tagVoListStr = JsonUtil.objToString(tagVoList);
        //存入jedis
        RedisShardedPoolUtil.set(Const.CacheTypeName.TAGS_WITHCOUNT, tagVoListStr);

        //分类
        List<CategoryVo> categoryList = categoryMapper.selectAllWithBlogCount();
        String categoryVoStr = JsonUtil.objToString(categoryList);
        RedisShardedPoolUtil.set(Const.CacheTypeName.CATEGORY_WITHCOUNT, categoryVoStr);

    }

    public List<TagVo> findTagsWithCount() {
        String tagsVoListJson = RedisShardedPoolUtil.get(Const.CacheTypeName.TAGS_WITHCOUNT);
        List<TagVo> tagVoList = JsonUtil.stringToObj(tagsVoListJson, List.class, TagVo.class);
        return tagVoList;
    }

    public List<CategoryVo> findAllCategoryWithBlogCount() {
        String catetoryVoListJson = RedisShardedPoolUtil.get(Const.CacheTypeName.CATEGORY_WITHCOUNT);
        List<CategoryVo> categoryVoList = JsonUtil.stringToObj(catetoryVoListJson, List.class, CategoryVo.class);
        return categoryVoList;
    }


    //REMOTE_USER_LIKE_STATUS ,remoteUserAddr_blogId , true/false
    public Boolean isLikeUserExists(String remoteUserAddr, Integer blogId) {
        Boolean result = RedisShardedPoolUtil.hexists(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS, remoteUserAddr + "_" + blogId.toString());
        return result;
    }

    public Long addLikeUser(String remoteUserAddr) {
        //todo
        Long result = RedisShardedPoolUtil.hset(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS, remoteUserAddr, Boolean.toString(true), Const.CacheTime.ADD_LIKE_TIME);

        return result;
    }

    public Boolean getUserLikeStatus(String remoteUserAddr) {
        Boolean result = JsonUtil.stringToObj(RedisShardedPoolUtil.hget(Const.CacheTypeName.REMOTE_USER_LIKE_STATUS, remoteUserAddr), Boolean.class);
        if (result == null || result == false) {
            return false;
        }
        return result;
    }

    public void addBlogViewer(String remoteUserAddr, Integer blogId) {
        RedisShardedPoolUtil.sadd(Const.CacheTypeName.REMOTE_VIEW_USER + "_" + remoteUserAddr, blogId.toString(), Const.CacheTime.VIEW_COUNT_TIME);
    }

    public boolean isBlogViewerExists(String remoteUserAddr, Integer blogId) {
        return RedisShardedPoolUtil.sismember(Const.CacheTypeName.REMOTE_VIEW_USER + "_" + remoteUserAddr, blogId.toString());
    }

    // 统计访客 存入Redis数据库
    public void addVisitor(String ip) {
        String key = Const.VISITOR.VISITOR_KEY;
        String filed = Const.VISITOR.VISITOR_IP_PREFIX + "_" + ip;
        Boolean isExists = RedisShardedPoolUtil.hexists(key, filed);

        Visitor visitor = new Visitor();
        Date curTime = new Date();
        if (isExists) {
            visitor = JsonUtil.stringToObj(RedisShardedPoolUtil.hget(key, filed), Visitor.class);
            visitor.setUpdatetime(curTime);
        } else {
            visitor.setIpaddr(ip);
            visitor.setCreatetime(curTime);
            long result = RedisShardedPoolUtil.incr(Const.VISITOR.VISITOR_COUNT);
        }
        RedisShardedPoolUtil.hset(key, filed, JsonUtil.objToString(visitor), Const.CacheTime.VISITOR_COUNT);
    }

    // 把访客存入数据库
    public void saveVisitorToDB(){
        List<Visitor> visitors = null;
        Map<String, String> map = RedisShardedPoolUtil.hgetAll(Const.VISITOR.VISITOR_KEY);
        if (map != null && map.size() > 0) {
            Collection<String> list = map.values();
            visitors = JsonUtil.stringToObj(list.toString(), List.class, Visitor.class);
        } else {
            return;
        }
        if (visitors != null && visitors.size() > 0) {
            visitorMapper.batchInsert(visitors);
            saveVisitorCount();
        }
    }

    public void saveVisitorCount()  {
        String visitorCount = RedisShardedPoolUtil.get(Const.VISITOR.VISITOR_COUNT);
        int count = 0;
        if (visitorCount != null && visitorCount.length() > 0) {
            count = Integer.parseInt(visitorCount);
            count += Integer.parseInt(PropertiesUtil.getProperty("visitor.count"));
            try {
                PropertiesUtil.setProperty("visitor.count", String.valueOf(count));
            } catch (IOException e) {
                log.error("saveVisitorCount{}",e);
            }
            RedisShardedPoolUtil.set(Const.VISITOR.VISITOR_BASIC,count+"");
        }
    }

}
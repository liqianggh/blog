package cn.blog.task;

import cn.blog.common.Const;
import cn.blog.service.CacheService.CacheService;
import cn.blog.util.PropertiesUtil;
import cn.blog.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TagsAndCategoryTask {
    @Autowired
    private CacheService tagCacheService;

    //@Scheduled(cron="0 0 */2 * * ?")//两个小时的倍数执行

//    @Scheduled(cron="0 */1 * * * ?")
    public void initCache(){
        log.info("执行缓存更新！");
        tagCacheService.initCache();
        log.info("缓存更新执行完毕！");
    }


    @Scheduled(cron="0 */1 * * * ?")
    public void initCacheV2(){
        log.info("执行缓存更新！");
        long timeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setNxResult = RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.REDIS_LOCK_NAME,String.valueOf(System.currentTimeMillis()+timeOut));

        if(setNxResult!=null)

        tagCacheService.initCache();
        log.info("缓存更新执行完毕！");
    }

}

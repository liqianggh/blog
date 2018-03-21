package cn.blog.task;

import cn.blog.common.Const;
import cn.blog.service.CacheService.CacheService;
import cn.blog.util.PropertiesUtil;
import cn.blog.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

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


    @Scheduled(cron="0 */10 * * * ?")
    public void initCacheV2(){
        log.info("执行缓存更新！");
        long timeOut = Long.parseLong(PropertiesUtil.getProperty("lock.timeout"));
        Long setNxResult = RedisShardedPoolUtil.setNx(Const.REDIS_LOCK.REDIS_LOCK_NAME,String.valueOf(System.currentTimeMillis()+timeOut));

        //获取锁成功
        if(setNxResult!=null&&setNxResult==1){
            initialCache(Const.REDIS_LOCK.REDIS_LOCK_NAME);
        }else{
            //获取锁失败
            /*
            * 校验锁是否过期
            * */
            Long timeOutResult = Long.parseLong(RedisShardedPoolUtil.get(Const.REDIS_LOCK.REDIS_LOCK_NAME));
            //如果过期时间不为空 并且已经过期
            if(timeOutResult!=null&&System.currentTimeMillis()>timeOutResult+timeOut){
                String getSetResult = RedisShardedPoolUtil.getset(Const.REDIS_LOCK.REDIS_LOCK_NAME,String.valueOf(System.currentTimeMillis()+timeOut));
                if(getSetResult==null||(getSetResult!=null&&Long.valueOf(getSetResult)==timeOutResult)){
                    initialCache(Const.REDIS_LOCK.REDIS_LOCK_NAME);
                    log.info("缓存更新执行完毕！");
                }else{
                    log.info("获取分布式锁失败！");

                }
            }else{
                log.info("获取分布式锁失败！");
            }
       }
//        tagCacheService.initCache();
    }

    public  void initialCache(String locakName){
        tagCacheService.initCache();
        RedisShardedPoolUtil.del(locakName);
    }




}

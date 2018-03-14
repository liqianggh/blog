package cn.blog.task;

import cn.blog.service.CacheService.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TagsAndCategoryTask {
    @Autowired
    private CacheService tagCacheService;

    @Scheduled(cron="0 0 */2 * * ?")//两个小时的倍数执行

//    @Scheduled(cron="0 */1 * * * ?")
    public void initCache(){
        log.info("执行缓存更新！");
        tagCacheService.initCache();
        log.info("缓存更新执行完毕！");
    }

}

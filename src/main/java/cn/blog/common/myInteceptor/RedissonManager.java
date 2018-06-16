package cn.blog.common.myInteceptor;

import cn.blog.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
  * @Description:  Redisson初始化类，注意，redisson不支持redis集群
  * Created by Jann on 2018/5/19  20:26.
  */
 @Component
 @Slf4j
public class RedissonManager {
    private Config config = new Config();
    private Redisson redisson = null;

    //    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
//    private static int redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static int redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    public Redisson getRedisson() {
        return redisson;
    }

    @PostConstruct
    private void init(){
        try {
            config.useSingleServer().setAddress(new StringBuilder(redis2Ip).append(":").append(redis2Port).toString());
            redisson = (Redisson) Redisson.create(config);
            log.info("初始化redisson完成");
        } catch (Exception e) {
            log.info("redisson 初始化失败",e);
        }
    }



 }

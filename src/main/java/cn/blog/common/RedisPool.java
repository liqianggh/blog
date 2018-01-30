package cn.blog.common;

        import cn.blog.util.PropertiesUtil;
        import redis.clients.jedis.Jedis;
        import redis.clients.jedis.JedisPool;
        import redis.clients.jedis.JedisPoolConfig;

        import java.util.Properties;

/**
 * @Description: 自定义连接池
 * Created by Jann Lee on 2018/1/27  3:58.
 */
public class RedisPool {
     private static JedisPool pool;//jedis连接池
     private static Integer maxTotal=Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
     private static Integer maxIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));//最大空闲Jedis实例个数
     private static Integer minIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));//最小空闲Jedis实例个数

     private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//获取jedis实例前是否要进行验证
     private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));//归还时是否进行验证

     private static String redisIp = PropertiesUtil.getProperty("redis.ip");
     private static int redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port","6379"));


     private static void initPool(){
      JedisPoolConfig config = new JedisPoolConfig();
      config.setMaxTotal(maxTotal);
      config.setMinIdle(minIdel);
      config.setMaxIdle(maxIdel);
      config.setTestOnReturn(testOnReturn);
      config.setTestOnBorrow(testOnBorrow);

      config.setBlockWhenExhausted(true);//连接耗尽是凑阻塞 默认为true

      pool = new JedisPool(config,redisIp,redisPort,2000);
     }

     static {
      initPool();
     }

     public static Jedis getJedis(){
      return pool.getResource();
     }

     public  static void returnResource(Jedis jedis){
      pool.returnResource(jedis);
     }

     public static void returnBrokenResource(Jedis jedis){
      pool.returnBrokenResource(jedis);
     }

}

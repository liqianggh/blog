package cn.blog.common;

import cn.blog.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

public class RedisShardedPool {
    private static ShardedJedisPool pool; //sharded jedis连接池
    private static Integer maxTotal=Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//最大连接数
    private static Integer maxIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));//最大空闲Jedis实例个数
    private static Integer minIdel=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));//最小空闲Jedis实例个数
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//获取jedis实例前是否要进行验证
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));//归还时是否进行验证

//    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
//    private static int redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));


    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static int redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private  static void initPool(){
        System.out.println(redis2Ip+":"+redis2Port);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setMaxTotal(maxTotal);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，默认为true

//        JedisShardInfo info1 = new JedisShardInfo(redis1Ip,redis1Port,1000*2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip,redis2Port,1000*2);
//        info1.setPassword("密码");

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
//        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        //创建连接池，指定分配算法
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();

        for(int i = 0; i < 3; i ++){
            jedis.set("key"+i,"value"+i);
        }

        returnResource(jedis);
        pool.destroy();
        System.out.println("program is end");
    }

}
package cn.blog.util;

import cn.blog.common.Const;
import cn.blog.common.RedisShardedPool;
import cn.blog.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

import java.util.Map;

/**
  * @Description: 封装Redis工具类
  * Created by Jann Lee on 2018/1/28  1:08.
  */
@Slf4j
public class RedisShardedPoolUtil {
    //set方法
    public static String set(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error{ }", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static String setEx(String key, String value,Integer expireTime) {
        ShardedJedis jedis = null;
        String result = null;
        if (expireTime == null) {
            expireTime = 60 * 30;
        }
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.set(key, value);
            jedis.expire(key,expireTime);
        } catch (Exception e) {
            log.error("set key:{} value:{} error{} expireTime{}", key, value, e,expireTime);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }
    //sadd方法
    public static Long sadd(String key, String value, Integer expireTime) {
        ShardedJedis jedis = null;
        Long result = null;
        if (expireTime == null) {
            expireTime = 60 * 30;
        }
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.sadd(key, value);
            jedis.expire(key, Const.CacheTime.VIEW_COUNT_TIME);
        } catch (Exception e) {
            log.error("sadd key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    //sismember 方法
    public static Boolean sismember(String key, String member) {
        ShardedJedis jedis = null;
        Boolean result = false;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.sismember(key, member);
        } catch (Exception e) {
            log.error("sismember key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //get方法
    public static String get(String key) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //设置过期时间 秒
    public static String setEx(String key, String value, int exTime) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("setex:{} value:{} exTime{} error", key, value, exTime, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    //设置有效期  单位是秒
    public static Long expire(String key, int exTime) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} exTIme:{} error", key, exTime, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static Long del(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //hset方法
    public static Long hset(String key, String filed, String value, Integer expireTime) {
        ShardedJedis jedis = null;
        Long result = null;
        if (expireTime == null) {
            expireTime = 60 * 30;
        }
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.hset(key, filed, value);
            jedis.expire(key, expireTime);
        } catch (Exception e) {
            log.error("hset key:{} filed:{} expireTime{} value:{} error", key, filed, value,expireTime, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //hexists方法
    public static Boolean hexists(String key, String filed) {
        ShardedJedis jedis = null;
        Boolean result = false;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.hexists(key, filed);
        } catch (Exception e) {
            log.error("hexists key:{} filed:{} error{}", key, filed, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //hget
    public static String hget(String key, String filed) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.hget(key, filed);
        } catch (Exception e) {
            log.error("hget key:{} filed:{} error{}", key, filed, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }
    //hdel
    public static Long hdel(String key, String filed) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.hdel(key, filed);
        } catch (Exception e) {
            log.error("hget key:{} filed:{} error{}", key, filed, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //hdel
    public static Map<String,String> hgetAll (String key) {
        ShardedJedis jedis = null;
        Map<String,String> result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result =  jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("hgetAll key:{}  error{}", key, e);
            RedisShardedPool.returnBrokenResource(jedis);
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    //setNx方法
    public static Long setNx(String key, String value) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error{}", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }
    public static Long setNx(String key, String value, int expire) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.setnx(key, value);
            jedis.expire(key,expire);
        } catch (Exception e) {
            log.error("set key:{} value:{} error:{}", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }
    public static String getset(String key, String value) {
        ShardedJedis jedis = null;
        String result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error", key, value, e);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static long incr(String key) {
        ShardedJedis jedis = null;
        Long result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.incr(key);
        } catch (Exception e) {
            log.error("incr key:{}", key);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }

    public static Boolean exists(String key) {
        ShardedJedis jedis = null;
        Boolean result = null;
        try {
            jedis = RedisShardedPool.getJedis();
            result = jedis.exists(key);
        } catch (Exception e) {
            log.error("incr exists:{}", key);
            RedisShardedPool.returnBrokenResource(jedis);
            return result;
        }
        RedisShardedPool.returnResource(jedis);
        return result;
    }


    public static void main(String[] args) {
        ShardedJedis jedis = RedisShardedPool.getJedis();

         for(int i = 0; i < 10;i++){
             jedis.sadd("jedis"+i,"value"+i);
         }

            RedisShardedPool.returnResource(jedis);
         System.out.println("end");

//        boolean result = RedisShardedPoolUtil.sismember("setTest", "valuseTest");
//        log.info(result + " ");
    }
}
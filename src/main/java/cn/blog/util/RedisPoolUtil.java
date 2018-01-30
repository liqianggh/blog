package cn.blog.util;

import cn.blog.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

  /**
   * @Description: 封装Redis工具类
   * Created by Jann Lee on 2018/1/30  21:19.
   */
@Slf4j
public class RedisPoolUtil {

    //set方法
    public static String set(String key,String value){
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    //get方法
     public static String get(String key){
         Jedis jedis = null;
         String result = null;
         try {
             jedis = RedisPool.getJedis();
             result = jedis.get(key);
         } catch (Exception e) {
             log.error("get key:{} error",key,e);
             RedisPool.returnBrokenResource(jedis);
             return result;
         }
         RedisPool.returnResource(jedis);
         return result;
     }

     //设置过期时间 秒
     public static String setEx(String key,String value,int exTime){
         Jedis jedis = null;
         String result = null;
         try {
             jedis = RedisPool.getJedis();
             result = jedis.setex(key,exTime,value);
         } catch (Exception e) {
             log.error("setex:{} value:{} exTime{} error",key,value,exTime,e);
             RedisPool.returnBrokenResource(jedis);
             return result;
         }
         RedisPool.returnResource(jedis);
         return result;
     }



     //设置有效期  单位是秒
     public static Long expire(String key,int exTime){
         Jedis jedis = null;
         Long result = null;
         try {
             jedis = RedisPool.getJedis();
             result = jedis.expire(key,exTime);
         } catch (Exception e) {
             log.error("expire key:{} exTIme:{} error",key,exTime,e);
             RedisPool.returnBrokenResource(jedis);
             return result;
         }
         RedisPool.returnResource(jedis);
         return result;
     }

     //设置有效期  单位是秒
     public static Long del(String key){
         Jedis jedis = null;
         Long result = null;
         try {
             jedis = RedisPool.getJedis();
             result = jedis.del(key);
         } catch (Exception e) {
             log.error("del key:{} error",key,e);
             RedisPool.returnBrokenResource(jedis);
             return result;
         }
         RedisPool.returnResource(jedis);
         return result;
     }
     public static void main(String [] args){
         Jedis jedis = RedisPool.getJedis();
         RedisPoolUtil.set("keyTest","valueTest");
         String value = RedisPoolUtil.get("keyTest");

         RedisPoolUtil.setEx("keyex","valuesEx",60*10);

         RedisPoolUtil.expire("keyTest",60*20);

         RedisPoolUtil.del("keyTest");

         System.out.println("end");
     }
 }

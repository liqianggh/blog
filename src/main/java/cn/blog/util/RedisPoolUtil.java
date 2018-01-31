package cn.blog.util;

import cn.blog.common.Const;
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

      //sadd方法
      public static Long sadd(String key,String value,Integer expireTime){
          Jedis jedis = null;
          Long result = null;
          if(expireTime==null){
              expireTime=60*30;
          }
          try {
              jedis = RedisPool.getJedis();
              result = jedis.sadd(key,value);
              jedis.expire(key, Const.CacheTime.VIEW_COUNT_TIME);
          } catch (Exception e) {
              log.error("sadd key:{} value:{} error",key,value,e);
              RedisPool.returnBrokenResource(jedis);
          }
          RedisPool.returnResource(jedis);
          return result;
      }



      //sismember 方法
      public static Boolean sismember (String key,String member){
          Jedis jedis = null;
          Boolean result = false;
          try {
              jedis = RedisPool.getJedis();
              result = jedis.sismember(key,member);
          } catch (Exception e) {
              log.error("sismember key:{} error",key,e);
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

      //hset方法
      public static Long hset(String key,String filed,String value,Integer expireTime){
          Jedis jedis = null;
          Long result = null;
          if(expireTime==null){
              expireTime=60*30;
          }
          try {
              jedis = RedisPool.getJedis();
              result = jedis.hset(key,filed,value);
              jedis.expire(key, expireTime);
          } catch (Exception e) {
              log.error("hset key:{} filed:{} value:{} error",key,filed,value,e);
              RedisPool.returnBrokenResource(jedis);
          }
          RedisPool.returnResource(jedis);
          return result;
      }

      //hexists方法
      public static Boolean hexists(String key,String filed){
          Jedis jedis = null;
          Boolean result = false;
          try {
              jedis = RedisPool.getJedis();
              result = jedis.hexists(key,filed);
          } catch (Exception e) {
              log.error("hexists key:{} filed:{} error{}",key,filed,e);
              RedisPool.returnBrokenResource(jedis);
          }
          RedisPool.returnResource(jedis);
          return result;
      }

      //hget
      public static String hget(String key,String filed){
          Jedis jedis = null;
          String result =null;
          try {
              jedis = RedisPool.getJedis();
              result = jedis.hget(key,filed);
          } catch (Exception e) {
              log.error("hget key:{} filed:{} error{}",key,filed,e);
              RedisPool.returnBrokenResource(jedis);
          }
          RedisPool.returnResource(jedis);
          return result;
      }
      //hdel
      public static Long hdel(String key,String filed){
          Jedis jedis = null;
          Long result =null;
          try {
              jedis = RedisPool.getJedis();
              result = jedis.hdel(key,filed);
          } catch (Exception e) {
              log.error("hget key:{} filed:{} error{}",key,filed,e);
              RedisPool.returnBrokenResource(jedis);
          }
          RedisPool.returnResource(jedis);
          return result;
      }




     public static void main(String [] args){
         Jedis jedis = RedisPool.getJedis();
//         RedisPoolUtil.sadd("setTest","valueTest",1000);
//         String value = RedisPoolUtil.get("keyTest");
//
//        RedisPoolUtil.setEx("keyex","valuesEx",60*10);
//
//         RedisPoolUtil.expire("keyTest",60*20);
//
//          RedisPoolUtil.del("keyTest");
//
//         System.out.println("end");


         boolean result = RedisPoolUtil.sismember("setTest","valuseTest");
         log.info(result+" ");
     }
 }

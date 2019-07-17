package cn.mycookies.common.cache;

/**
 * 缓存抽象类
 *
 * @author Jann Lee
 * @date 2019-07-17 22:38
 **/
public interface CacheService<T> {

    /**
     * 增加并返回
     * @param key
     */
    boolean isDataExists(String key);

    /**
     * 针对数字类型的先增加后返回
     * @param key
     * @return
     */
    long incrAndGet(String key);

    /**
     * 获取数据
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 存数据
     * @param key
     * @param value
     * @return
     */
    boolean set(String key, T value);

}

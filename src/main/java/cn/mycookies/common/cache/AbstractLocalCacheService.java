package cn.mycookies.common.cache;

import com.google.common.cache.LoadingCache;
import com.mysql.jdbc.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * guava 实现的缓存
 *
 * @author Jann Lee
 * @date 2019-07-17 23:00
 **/
@Service
@Slf4j
public abstract class AbstractLocalCacheService implements CacheService<Long> {

    @Override
    public boolean isDataExists(String key) {
        return Objects.equals(get(key), 0L);
    }

    @Override
    public long incrAndGet(String key) {
        try {
            return initCacheHolder().get(key).incrementAndGet();
        } catch (ExecutionException e) {
            log.error("操作缓存失败,incrAndGet, key:{}",key);
        }
        return 0L;
    }

    @Override
    public Long get(String key) {
        try {
            return initCacheHolder().get(key).get();
        } catch (ExecutionException e) {
            log.error("操作缓存失败,get, key:{}",key);
        }
        return 0L;
    }

    @Override
    public boolean set(String key, Long value) {
        if (StringUtils.isNullOrEmpty(key)) {
            log.warn("操作缓存异常,get, key:{}",key);
            return false;
        }
        initCacheHolder().put(key, new AtomicLong(value));
        return true;
    }

    /**
     *  从字类中获取cache对象
     * @return
     */
    public abstract LoadingCache<String, AtomicLong> initCacheHolder();
}

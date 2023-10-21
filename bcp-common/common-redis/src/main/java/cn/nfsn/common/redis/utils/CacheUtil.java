package cn.nfsn.common.redis.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.nfsn.common.redis.model.domain.RedisData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static cn.nfsn.common.redis.constant.CacheConstants.*;

/**
 * @ClassName: CacheUtil
 * @Description: Redis 工具类
 * @Author: atnibamaitay
 * @CreateTime: 2023-09-09 22:05
 **/
@Slf4j
@Component
public class CacheUtil {

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private final StringRedisTemplate stringRedisTemplate;

    public CacheUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 逻辑过期
     *
     * @param key   键
     * @param value 值
     * @param time  先传时间值
     * @param unit  再传单位，下面也一样
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 缓存穿透
     *
     * @param keyPrefix     查redis时key的前缀
     * @param id            根据id查数据库的时候用的，不过因为id的类型不确定，所以也用泛型
     * @param type          要返回的类型
     * @param dbFallback    需要调用者提供数据库查询的逻辑
     * @param time          先传时间值
     * @param unit          再传单位
     * @param cacheNullTtl  缓存空值的存活时间，单位为分钟
     * @param cacheNullUnit 缓存空值的存活时间单位
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithPassThrough(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit, long cacheNullTtl, TimeUnit cacheNullUnit) {
        String key = keyPrefix + id;
        // 1.从redis查询
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(json)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(json, type);
        }
        // 判断命中的是否是空值
        if (json != null) {
            // 返回一个错误信息
            return null;
        }

        return queryAndCache(key, dbFallback, time, unit, id);
    }

    /**
     * 逻辑过期解决缓存击穿
     *
     * @param keyPrefix   查redis时key的前缀
     * @param id          根据id查数据库的时候用的，不过因为id的类型不确定，所以也用泛型
     * @param type        要返回的类型
     * @param dbFallback  需要调用者提供数据库查询的逻辑
     * @param time        先传时间值
     * @param unit        再传单位
     * @param lockTimeout 锁的超时时间
     * @param lockUnit    锁的超时时间单位
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithLogicalExpire(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit, long lockTimeout, TimeUnit lockUnit) {
        String key = keyPrefix + id;
        // 1.从redis查询
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isBlank(json)) {
            // 3.存在，直接返回
            return null;
        }
        // 4.命中，需要先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();
        // 5.判断是否过期
        if (expireTime.isAfter(LocalDateTime.now())) {
            // 5.1.未过期，直接返回信息
            return r;
        }
        // 5.2.已过期，需要缓存重建
        // 6.缓存重建
        // 6.1.获取互斥锁
        String lockKey = LOCK_KEY + id;
        boolean isLock = tryLock(lockKey, lockTimeout, lockUnit);
        // 6.2.判断是否获取锁成功
        if (isLock) {
            // 6.3.成功，开启独立线程，实现缓存重建
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 查询数据库
                    R newR = dbFallback.apply(id);
                    // 重建缓存
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    log.error("Error while rebuilding cache", e);
                } finally {
                    // 释放锁
                    unlock(lockKey);
                }
            });
        }
        // 6.4.返回过期的信息
        return r;
    }

    /**
     * 互斥锁解决缓存击穿
     *
     * @param keyPrefix   查redis时key的前缀
     * @param id          根据id查数据库的时候用的，不过因为id的类型不确定，所以也用泛型
     * @param type        要返回的类型
     * @param dbFallback  需要调用者提供数据库查询的逻辑
     * @param time        先传时间值
     * @param unit        再传单位
     * @param lockTimeout 锁的超时时间
     * @param lockUnit    锁的超时时间单位
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithMutex(
            String keyPrefix, ID id, Class<R> type, Function<ID, R> dbFallback, Long time, TimeUnit unit, long lockTimeout, TimeUnit lockUnit) {
        String key = keyPrefix + id;
        // 1.从redis查询
        String cacheJson = stringRedisTemplate.opsForValue().get(key);
        // 2.判断是否存在
        if (StrUtil.isNotBlank(cacheJson)) {
            // 3.存在，直接返回
            return JSONUtil.toBean(cacheJson, type);
        }
        // 判断命中的是否是空值
        if (cacheJson != null) {
            // 返回一个错误信息
            return null;
        }

        // 4.实现缓存重建
        // 4.1.获取互斥锁
        String lockKey = LOCK_KEY + id;
        R r = null;
        while (true) {
            try {
                boolean isLock = tryLock(lockKey, lockTimeout, lockUnit);
                // 4.2.判断是否获取成功
                if (isLock) {
                    r = queryAndCache(key, dbFallback, time, unit, id);
                    // 成功获取数据后跳出循环
                    break;
                } else {
                    // 获取锁失败，休眠后继续尝试
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                log.error("Interrupted exception occurred while trying to acquire the lock", e);
            } finally {
                // 7.释放锁
                unlock(lockKey);
            }
        }
        // 8.返回
        return r;
    }

    /**
     * 根据给定的键从数据库查询数据，并将其缓存在Redis中。
     *
     * @param key        指定的key，用于在Redis中进行数据存储
     * @param dbFallback 数据库查询功能接口，当Redis缓存中不存在数据时，会通过此接口进行数据库查询
     * @param time       缓存数据的有效期长度
     * @param unit       缓存数据的有效期单位
     * @param id         数据库查询参数
     * @param <R>        数据库查询结果类型
     * @param <ID>       数据库查询参数类型
     * @return 返回数据库查询的结果，如果没有查到数据，则返回null
     */
    private <R, ID> R queryAndCache(String key, Function<ID, R> dbFallback, Long time, TimeUnit unit, ID id) {
        // 使用提供的键和数据库查询回调函数进行数据库查询
        R r = dbFallback.apply(id);

        // 如果数据库查询结果为空
        if (r == null) {
            // 将空值写入redis，用以标记该键下无可用数据，防止缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            // 返回null，表示数据库中无对应数据
            return null;
        }
        // 将数据库查询结果写入redis进行缓存
        this.set(key, r, time, unit);

        // 返回数据库查询结果
        return r;
    }

    /**
     * 尝试加锁
     *
     * @param key     锁
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=加锁成功；false=加锁失败
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", timeout, unit);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 尝试加锁
     *
     * @param key 锁
     * @return true=加锁成功；false=加锁失败
     */
    public void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
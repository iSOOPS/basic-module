package com.isoops.basicmodule.redis.source;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 该加锁方法仅针对单实例 Redis 可实现分布式加锁
     * 对于 Redis 集群则无法使用
     * <p>
     * 支持重复，线程安全
     *
     */
    public boolean lock(String lockKey, String clientId,Integer seconds) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, clientId, seconds, TimeUnit.SECONDS);
    }
    /**
     * 与 tryLock 相对应，用作释放锁
     */
    public boolean unLock(String key) {
        return redisTemplate.opsForValue().getOperations().delete(key);
    }
}

package com.yuxian.yubi.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtils {
 
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
 
    /**
     * 读取值类型为字符串
     *
     * @param key
     * @return
     */
    public String getString(final String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null : value.toString();
    }

    /**
     * 读取key匹配某个规则的key列表
     *
     * @param pattern
     * @return
     */
    public Set<String> getByPattern(final String pattern) {
		return redisTemplate.keys(pattern);
	}
    /**
     * 写入值类型为字符串的缓存
     */
    public boolean setString(final String key, String value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入值类型为Set集合的缓存
     */
    public boolean setSet(final String key, Object value) {
        long result = 0L;
        try {
            result = Objects.requireNonNull(redisTemplate.opsForSet().add(key, value));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result == 1L;
    }

    /**
     * 写入值类型为Set集合的缓存,并设置过期时间
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    public boolean setSet(final String key, long value, long timeout, TimeUnit unit) {
        long result = 0L;
        try {
            result = Objects.requireNonNull(redisTemplate.opsForSet().add(key, value));
            redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result == 1L;
    }


    /**
     * 写入值类型为字符串的缓存,并设置过期时间
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     * @return
     */
    public boolean setString(final String key, String value, long timeout, TimeUnit unit) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
 

    /**
     * 删除缓存
     */
    public boolean delete(final String key) {
        boolean result = false;
        try {
            redisTemplate.delete(key);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 增加
     */
    public Long incr(final String key, long delta) {
        if (delta < 0) {
			throw new RuntimeException("递增数据必须大于0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 减少
     */
    public Long decr(final String key, long delta) {
		return redisTemplate.opsForValue().decrement(key, delta);
    }

    public boolean hasKey(final String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

}
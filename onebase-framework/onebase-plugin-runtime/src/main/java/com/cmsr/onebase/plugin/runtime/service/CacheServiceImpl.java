package com.cmsr.onebase.plugin.runtime.service;

import com.cmsr.onebase.plugin.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现
 * <p>
 * 基于Redis的缓存服务实现。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Service
public class CacheServiceImpl implements CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheServiceImpl.class);

    private StringRedisTemplate redisTemplate;

    public CacheServiceImpl() {
        // 无参构造，便于无Redis时的降级
    }

    public CacheServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Resource
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        log.debug("CacheService.get: key={}", key);
        if (redisTemplate == null) {
            return null;
        }
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        T value = get(key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void set(String key, Object value) {
        log.debug("CacheService.set: key={}", key);
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, String.valueOf(value));
        }
    }

    @Override
    public void set(String key, Object value, Duration duration) {
        log.debug("CacheService.set: key={}, duration={}", key, duration);
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, String.valueOf(value), duration);
        }
    }

    @Override
    public void set(String key, Object value, long seconds) {
        set(key, value, Duration.ofSeconds(seconds));
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        log.debug("CacheService.setIfAbsent: key={}", key);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value));
        return result != null && result;
    }

    @Override
    public boolean setIfAbsent(String key, Object value, Duration duration) {
        log.debug("CacheService.setIfAbsent: key={}, duration={}", key, duration);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value), duration);
        return result != null && result;
    }

    @Override
    public boolean delete(String key) {
        log.debug("CacheService.delete: key={}", key);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.delete(key);
        return result != null && result;
    }

    @Override
    public long delete(Collection<String> keys) {
        log.debug("CacheService.delete: keys count={}", keys.size());
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.delete(keys);
        return result != null ? result : 0;
    }

    @Override
    public boolean exists(String key) {
        log.debug("CacheService.exists: key={}", key);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    @Override
    public boolean expire(String key, Duration duration) {
        log.debug("CacheService.expire: key={}, duration={}", key, duration);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.expire(key, duration);
        return result != null && result;
    }

    @Override
    public long getExpire(String key) {
        log.debug("CacheService.getExpire: key={}", key);
        if (redisTemplate == null) {
            return -2;
        }
        Long result = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return result != null ? result : -2;
    }

    @Override
    public long increment(String key) {
        return increment(key, 1);
    }

    @Override
    public long increment(String key, long delta) {
        log.debug("CacheService.increment: key={}, delta={}", key, delta);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return result != null ? result : 0;
    }

    @Override
    public long decrement(String key) {
        return decrement(key, 1);
    }

    @Override
    public long decrement(String key, long delta) {
        log.debug("CacheService.decrement: key={}, delta={}", key, delta);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForValue().decrement(key, delta);
        return result != null ? result : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hashGet(String key, String field) {
        log.debug("CacheService.hashGet: key={}, field={}", key, field);
        if (redisTemplate == null) {
            return null;
        }
        return (T) redisTemplate.opsForHash().get(key, field);
    }

    @Override
    public List<Object> hashMultiGet(String key, Collection<String> fields) {
        log.debug("CacheService.hashMultiGet: key={}, fields={}", key, fields);
        if (redisTemplate == null) {
            return Collections.emptyList();
        }
        return redisTemplate.opsForHash().multiGet(key, (Collection) fields);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> hashGetAll(String key) {
        log.debug("CacheService.hashGetAll: key={}", key);
        if (redisTemplate == null) {
            return Collections.emptyMap();
        }
        return (Map<String, Object>) (Map) redisTemplate.opsForHash().entries(key);
    }

    @Override
    public void hashSet(String key, String field, Object value) {
        log.debug("CacheService.hashSet: key={}, field={}", key, field);
        if (redisTemplate != null) {
            redisTemplate.opsForHash().put(key, field, String.valueOf(value));
        }
    }

    @Override
    public void hashSetAll(String key, Map<String, Object> map) {
        log.debug("CacheService.hashSetAll: key={}", key);
        if (redisTemplate != null) {
            redisTemplate.opsForHash().putAll(key, map);
        }
    }

    @Override
    public long hashDelete(String key, String... fields) {
        log.debug("CacheService.hashDelete: key={}, fields={}", key, fields);
        if (redisTemplate == null) {
            return 0;
        }
        return redisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    @Override
    public boolean hashExists(String key, String field) {
        log.debug("CacheService.hashExists: key={}, field={}", key, field);
        if (redisTemplate == null) {
            return false;
        }
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    @Override
    public long listLeftPush(String key, Object value) {
        log.debug("CacheService.listLeftPush: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForList().leftPush(key, String.valueOf(value));
        return result != null ? result : 0;
    }

    @Override
    public long listRightPush(String key, Object value) {
        log.debug("CacheService.listRightPush: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForList().rightPush(key, String.valueOf(value));
        return result != null ? result : 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T listLeftPop(String key) {
        log.debug("CacheService.listLeftPop: key={}", key);
        if (redisTemplate == null) {
            return null;
        }
        return (T) redisTemplate.opsForList().leftPop(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T listRightPop(String key) {
        log.debug("CacheService.listRightPop: key={}", key);
        if (redisTemplate == null) {
            return null;
        }
        return (T) redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public List<Object> listRange(String key, long start, long end) {
        log.debug("CacheService.listRange: key={}, start={}, end={}", key, start, end);
        if (redisTemplate == null) {
            return Collections.emptyList();
        }
        List<String> list = redisTemplate.opsForList().range(key, start, end);
        return list != null ? (List) list : Collections.emptyList();
    }

    @Override
    public long listSize(String key) {
        log.debug("CacheService.listSize: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForList().size(key);
        return result != null ? result : 0;
    }

    @Override
    public long setAdd(String key, Object... values) {
        log.debug("CacheService.setAdd: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        String[] stringValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            stringValues[i] = String.valueOf(values[i]);
        }
        Long result = redisTemplate.opsForSet().add(key, stringValues);
        return result != null ? result : 0;
    }

    @Override
    public long setRemove(String key, Object... values) {
        log.debug("CacheService.setRemove: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForSet().remove(key, values);
        return result != null ? result : 0;
    }

    @Override
    public boolean setIsMember(String key, Object value) {
        log.debug("CacheService.setIsMember: key={}, value={}", key, value);
        if (redisTemplate == null) {
            return false;
        }
        Boolean result = redisTemplate.opsForSet().isMember(key, value);
        return result != null && result;
    }

    @Override
    public Set<Object> setMembers(String key) {
        log.debug("CacheService.setMembers: key={}", key);
        if (redisTemplate == null) {
            return new HashSet<>();
        }
        Set<String> members = redisTemplate.opsForSet().members(key);
        return members != null ? (Set) members : new HashSet<>();
    }

    @Override
    public long setSize(String key) {
        log.debug("CacheService.setSize: key={}", key);
        if (redisTemplate == null) {
            return 0;
        }
        Long result = redisTemplate.opsForSet().size(key);
        return result != null ? result : 0;
    }
}

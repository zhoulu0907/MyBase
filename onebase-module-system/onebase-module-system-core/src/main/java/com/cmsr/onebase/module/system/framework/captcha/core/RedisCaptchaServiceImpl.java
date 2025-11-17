package com.cmsr.onebase.module.system.framework.captcha.core;

import com.anji.captcha.service.CaptchaCacheService;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.module.infra.api.security.SecurityConfigApi;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 基于 Redis 实现验证码的存储
 *
 * @author 星语
 */
@Setter
@Slf4j
public class RedisCaptchaServiceImpl implements CaptchaCacheService {

    private StringRedisTemplate stringRedisTemplate;

    private SecurityConfigApi securityConfigApi;

    @Override
    public String type() {
        return "redis";
    }

    @Override
    public void set(String key, String value, long expiresInSeconds) {
        // 获取租户配置的验证码有效期
        long actualExpiry = expiresInSeconds; // 默认使用aj-captcha的配置
        
        try {
            if (securityConfigApi != null) {
                CommonResult<Integer> result = securityConfigApi.getCaptchaExpirySeconds();
                if (result != null && result.isSuccess() && result.getData() != null) {
                    actualExpiry = result.getData();
                    log.debug("使用租户配置的验证码有效期: {}秒", actualExpiry);
                }
            }
        } catch (Exception e) {
            log.warn("获取租户验证码有效期配置失败，使用默认值: {}秒, 错误: {}", expiresInSeconds, e.getMessage());
        }
        
        stringRedisTemplate.opsForValue().set(key, value, actualExpiry, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Long increment(String key, long val) {
        return stringRedisTemplate.opsForValue().increment(key,val);
    }

}

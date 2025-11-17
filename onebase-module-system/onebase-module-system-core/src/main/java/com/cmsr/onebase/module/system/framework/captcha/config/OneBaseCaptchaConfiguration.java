package com.cmsr.onebase.module.system.framework.captcha.config;

import com.cmsr.onebase.module.infra.api.security.SecurityConfigApi;
import com.cmsr.onebase.module.system.framework.captcha.core.RedisCaptchaServiceImpl;
import com.anji.captcha.config.AjCaptchaAutoConfiguration;
import com.anji.captcha.properties.AjCaptchaProperties;
import com.anji.captcha.service.CaptchaCacheService;
import com.anji.captcha.service.impl.CaptchaServiceFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 验证码的配置类
 *
 */
@Configuration(proxyBeanMethods = false)
@ImportAutoConfiguration(AjCaptchaAutoConfiguration.class) // 目的：解决 aj-captcha 针对 SpringBoot 3.X 自动配置不生效的问题
public class OneBaseCaptchaConfiguration {

    @Resource
    private SecurityConfigApi securityConfigApi;

    @Bean(name = "AjCaptchaCacheService")
    @Primary
    public CaptchaCacheService captchaCacheService(AjCaptchaProperties config,
                                                   StringRedisTemplate stringRedisTemplate) {
        CaptchaCacheService captchaCacheService = CaptchaServiceFactory.getCache(config.getCacheType().name());
        if (captchaCacheService instanceof RedisCaptchaServiceImpl) {
            RedisCaptchaServiceImpl redisCaptchaService = (RedisCaptchaServiceImpl) captchaCacheService;
            redisCaptchaService.setStringRedisTemplate(stringRedisTemplate);
            redisCaptchaService.setSecurityConfigApi(securityConfigApi);
        }
        return captchaCacheService;
    }

}

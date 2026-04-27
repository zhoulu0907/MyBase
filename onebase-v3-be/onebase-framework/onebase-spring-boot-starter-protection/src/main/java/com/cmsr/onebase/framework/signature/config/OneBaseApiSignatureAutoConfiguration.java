package com.cmsr.onebase.framework.signature.config;

import com.cmsr.onebase.framework.redis.config.OneBaseRedisAutoConfiguration;
import com.cmsr.onebase.framework.signature.core.ApiSinatureFilter;
import com.cmsr.onebase.framework.signature.core.aop.ApiSignHelper;
import com.cmsr.onebase.framework.signature.core.redis.ApiSignatureRedisDAO;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * HTTP API 签名的自动配置类
 *
 * @author Zhougang
 */
@AutoConfiguration(after = OneBaseRedisAutoConfiguration.class)
@EnableConfigurationProperties(ApiSignatureProperties.class)
public class OneBaseApiSignatureAutoConfiguration {

    @Bean
    public ApiSinatureFilter apiSinatureFilter() {
        return new ApiSinatureFilter();
    }

    @Bean
    public ApiSignHelper apiSignHelper(ApiSignatureProperties properties) {
        return new ApiSignHelper(properties);
    }

    @Bean
    public ApiSignatureRedisDAO signatureRedisDAO(StringRedisTemplate stringRedisTemplate) {
        return new ApiSignatureRedisDAO(stringRedisTemplate);
    }

    // @Bean
    // public ApiSignatureAspect signatureAspect(ApiSignatureRedisDAO signatureRedisDAO) {
    //     return new ApiSignatureAspect(signatureRedisDAO);
    // }
    //


}

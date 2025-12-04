package com.cmsr.onebase.framework.tenant.config;

import cn.hutool.extra.spring.SpringUtil;
import com.cmsr.onebase.framework.common.biz.system.tenant.TenantCommonApi;
import com.cmsr.onebase.framework.common.enums.WebFilterOrderEnum;
import com.cmsr.onebase.framework.redis.config.OneBaseCacheProperties;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnoreAspect;
import com.cmsr.onebase.framework.tenant.core.redis.TenantRedisCacheManager;
import com.cmsr.onebase.framework.tenant.core.security.TenantSecurityWebFilter;
import com.cmsr.onebase.framework.tenant.core.service.TenantFrameworkService;
import com.cmsr.onebase.framework.tenant.core.service.TenantFrameworkServiceImpl;
import com.cmsr.onebase.framework.tenant.core.web.TenantVisitContextInterceptor;
import com.cmsr.onebase.framework.web.config.WebProperties;
import com.cmsr.onebase.framework.web.core.handler.GlobalExceptionHandler;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Map;
import java.util.Objects;

import static com.cmsr.onebase.framework.common.util.collection.CollectionUtils.convertList;

@AutoConfiguration
@ConditionalOnProperty(prefix = "onebase.tenant", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(TenantProperties.class)
public class OneBaseTenantAutoConfiguration {

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    public TenantFrameworkService tenantFrameworkService(TenantCommonApi tenantApi) {
        try {
            TenantCommonApi tenantApiImpl = SpringUtil.getBean("tenantApiImpl", TenantCommonApi.class);
            if (tenantApiImpl != null) {
                tenantApi = tenantApiImpl;
            }
        } catch (Exception ignored) {
        }
        return new TenantFrameworkServiceImpl(tenantApi);
    }

    // ========== AOP ==========

    @Bean
    public TenantIgnoreAspect tenantIgnoreAspect() {
        return new TenantIgnoreAspect();
    }

    // ========== WEB ==========
    // @Bean
    // public FilterRegistrationBean<TenantContextWebFilter> tenantContextWebFilter(TenantProperties tenantProperties) {
    //     FilterRegistrationBean<TenantContextWebFilter> registrationBean = new FilterRegistrationBean<>();
    //     registrationBean.setFilter(new TenantContextWebFilter());
    //     registrationBean.setOrder(WebFilterOrderEnum.TENANT_CONTEXT_FILTER);
    //     addIgnoreUrls(tenantProperties);
    //     return registrationBean;
    // }

    /**
     * 如果 Controller 接口上，有 {@link TenantIgnore} 注解，那么添加到忽略的 URL 中
     *
     * @param tenantProperties 租户配置
     */
    private void addIgnoreUrls(TenantProperties tenantProperties) {
        // 获得接口对应的 HandlerMethod 集合
        RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)
                applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        // 获得有 @TenantIgnore 注解的接口
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (!handlerMethod.hasMethodAnnotation(TenantIgnore.class)) {
                continue;
            }
            // 添加到忽略的 URL 中
            if (entry.getKey().getPatternsCondition() != null) {
                tenantProperties.getIgnoreUrls().addAll(entry.getKey().getPatternsCondition().getPatterns());
            }
            if (entry.getKey().getPathPatternsCondition() != null) {
                tenantProperties.getIgnoreUrls().addAll(
                        convertList(entry.getKey().getPathPatternsCondition().getPatterns(), PathPattern::getPatternString));
            }
        }
    }

    @Bean
    public TenantVisitContextInterceptor tenantVisitContextInterceptor(TenantProperties tenantProperties) {
        return new TenantVisitContextInterceptor(tenantProperties);
    }

    @Bean
    public WebMvcConfigurer tenantWebMvcConfigurer(TenantProperties tenantProperties,
                                                   TenantVisitContextInterceptor tenantVisitContextInterceptor) {
        return new WebMvcConfigurer() {

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(tenantVisitContextInterceptor)
                        .excludePathPatterns(tenantProperties.getIgnoreVisitUrls().toArray(new String[0]));
            }
        };
    }

    // ========== Security ==========

    @Bean
    public FilterRegistrationBean<TenantSecurityWebFilter> tenantSecurityWebFilter(TenantProperties tenantProperties,
                                                                                   WebProperties webProperties,
                                                                                   GlobalExceptionHandler globalExceptionHandler,
                                                                                   TenantFrameworkService tenantFrameworkService) {
        FilterRegistrationBean<TenantSecurityWebFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TenantSecurityWebFilter(tenantProperties, webProperties,
                globalExceptionHandler, tenantFrameworkService));
        registrationBean.setOrder(WebFilterOrderEnum.TENANT_SECURITY_FILTER);
        return registrationBean;
    }

    // ========== Redis ==========

    @Bean
    @Primary // 引入租户时，tenantRedisCacheManager 为主 Bean
    public RedisCacheManager tenantRedisCacheManager(RedisTemplate<String, Object> redisTemplate,
                                                     RedisCacheConfiguration redisCacheConfiguration,
                                                     OneBaseCacheProperties oneBaseCacheProperties,
                                                     TenantProperties tenantProperties) {
        // 创建 RedisCacheWriter 对象
        RedisConnectionFactory connectionFactory = Objects.requireNonNull(redisTemplate.getConnectionFactory());
        RedisCacheWriter cacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory,
                BatchStrategies.scan(oneBaseCacheProperties.getRedisScanBatchSize()));
        // 创建 TenantRedisCacheManager 对象
        return new TenantRedisCacheManager(cacheWriter, redisCacheConfiguration, tenantProperties.getIgnoreCaches());
    }

}

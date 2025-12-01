package com.cmsr.onebase.framework.web.config;

import com.cmsr.onebase.framework.common.biz.infra.logger.ApiErrorLogCommonApi;
import com.cmsr.onebase.framework.common.enums.WebFilterOrderEnum;
import com.cmsr.onebase.framework.web.core.filter.CacheRequestBodyFilter;
import com.cmsr.onebase.framework.web.core.filter.LogMdcFilter;
import com.cmsr.onebase.framework.web.core.handler.GlobalExceptionHandler;
import com.cmsr.onebase.framework.web.core.util.WebFrameworkUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.modelmapper.ModelMapper;

@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class OneBaseWebAutoConfiguration implements WebMvcConfigurer {

    @Resource
    private WebProperties webProperties;
    /**
     * 应用名
     */
    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer) {
        configurePathMatch(configurer, webProperties.getPlatformApi());
        configurePathMatch(configurer, webProperties.getBuildApi());
        configurePathMatch(configurer, webProperties.getRuntimeApi());
    }

    /**
     * 设置 API 前缀，仅仅匹配 controller 包下的
     *
     * @param configurer 配置
     * @param api        API 配置
     */
    private void configurePathMatch(PathMatchConfigurer configurer, WebProperties.Api api) {
        AntPathMatcher antPathMatcher = new AntPathMatcher(".");
    configurer.addPathPrefix(api.getPrefix(), clazz -> clazz.isAnnotationPresent(RestController.class)
        && java.util.Arrays.stream(api.getController())
        .anyMatch(pattern -> antPathMatcher.match(pattern, clazz.getPackage().getName()))
    ); // 仅仅匹配 controller 包（支持任意数量的包匹配规则）
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler(ApiErrorLogCommonApi apiErrorLogApi) {
        return new GlobalExceptionHandler(applicationName, apiErrorLogApi, webProperties);
    }


    @Bean
    @SuppressWarnings("InstantiationOfUtilityClass")
    public WebFrameworkUtils webFrameworkUtils(WebProperties webProperties) {
        // 由于 WebFrameworkUtils 需要使用到 webProperties 属性，所以注册为一个 Bean
        return new WebFrameworkUtils(webProperties);
    }

    /**
     * 创建 ModelMapper Bean，用于对象转换
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        // 配置标准匹配策略，避免过于宽松的匹配导致冲突
        mapper.getConfiguration()
                .setMatchingStrategy(org.modelmapper.convention.MatchingStrategies.STANDARD)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setAmbiguityIgnored(true); // 忽略歧义映射，避免多个源属性匹配同一个目标属性
        return mapper;
    }

    // ========== Filter 相关 ==========

    /**
     * 创建 CorsFilter Bean，解决跨域问题
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        // 创建 CorsConfiguration 对象
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*"); // 设置访问源地址
        config.addAllowedHeader("*"); // 设置访问源请求头
        config.addAllowedMethod("*"); // 设置访问源请求方法
        // 创建 UrlBasedCorsConfigurationSource 对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 对接口配置跨域设置
        return createFilterBean(new CorsFilter(source), WebFilterOrderEnum.CORS_FILTER);
    }

    /**
     * 创建 RequestBodyCacheFilter Bean，可重复读取请求内容
     */
    @Bean
    public FilterRegistrationBean<CacheRequestBodyFilter> requestBodyCacheFilter() {
        return createFilterBean(new CacheRequestBodyFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER);
    }

    /**
     * 创建 LogMdcFilter Bean，用于在日志中添加请求相关信息
     */
    @Bean
    public FilterRegistrationBean<LogMdcFilter> logMdcFilter() {
        return createFilterBean(new LogMdcFilter(), WebFilterOrderEnum.REQUEST_BODY_CACHE_FILTER + 1);
    }


    public static <T extends Filter> FilterRegistrationBean<T> createFilterBean(T filter, Integer order) {
        FilterRegistrationBean<T> bean = new FilterRegistrationBean<>(filter);
        bean.setOrder(order);
        return bean;
    }


}

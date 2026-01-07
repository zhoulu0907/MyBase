package com.cmsr.onebase.framework.common.config;

import com.cmsr.onebase.framework.common.converter.EmptyStringToNullDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson配置类
 *
 * <p>统一配置Jackson序列化和反序列化行为，包括：
 * <ul>
 *   <li>空字符串自动转换为null</li>
 *   <li>其他通用配置</li>
 * </ul>
 * </p>
 *
 * @author matianyu
 * @date 2025-12-29
 */
@Configuration
public class JacksonAutoConfiguration {

    /**
     * 配置ObjectMapper，注册自定义反序列化器
     *
     * @param builder Jackson2ObjectMapperBuilder
     * @return 配置好的ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        
        // 注册自定义模块
        SimpleModule module = new SimpleModule();
        // 将空字符串反序列化为null
        module.addDeserializer(String.class, new EmptyStringToNullDeserializer());
        objectMapper.registerModule(module);
        
        return objectMapper;
    }
}

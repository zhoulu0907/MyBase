package com.cmsr.onebase.framework.desensitize.serializer;

import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.common.util.spring.SpringContextHolder;
import com.cmsr.onebase.framework.desensitize.config.DesensitizeProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Set;

/**
 * 用户名脱敏序列化器
 *
 * 脱敏规则：前1位 + **（如：w**）
 *
 * 配置优先级：
 * 1. 配置文件 onebase.desensitize.fields（优先）
 * 2. 数据库租户配置
 */
public class UsernameJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        // 检查是否需要脱敏
        if (!shouldDesensitize("username")) {
            gen.writeString(value);
            return;
        }

        // 用户名脱敏：前1位 + **
        String masked = value.substring(0, 1) + "**";
        gen.writeString(masked);
    }

    /**
     * 判断字段是否需要脱敏
     * 优先使用配置文件配置，否则使用数据库配置
     */
    private boolean shouldDesensitize(String fieldName) {
        if (!SpringContextHolder.isInitialized()) {
            return false;
        }

        // 尝试获取配置属性
        DesensitizeProperties properties = SpringContextHolder.getBean(DesensitizeProperties.class);
        if (properties != null && properties.useConfigFirst()) {
            return properties.getDesensitizeFields().contains(fieldName);
        }

        // 使用数据库配置
        SecurityConfigApi securityConfigApi = SpringContextHolder.getBean(SecurityConfigApi.class);
        if (securityConfigApi != null) {
            Set<String> tenantConfigValues = securityConfigApi.getTenantDesensitizedFieldValues();
            return tenantConfigValues.contains(fieldName);
        }

        return false;
    }
}
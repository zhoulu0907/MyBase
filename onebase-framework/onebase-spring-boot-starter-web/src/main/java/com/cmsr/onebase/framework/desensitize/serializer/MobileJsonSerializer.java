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
 * 手机号脱敏序列化器
 *
 * 脱敏规则：前3位 + **** + 后4位（如：138****1234）
 *
 * 配置优先级：
 * 1. 配置文件 onebase.desensitize.fields（优先）
 * 2. 数据库租户配置
 */
public class MobileJsonSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (StringUtils.isEmpty(value)) {
            gen.writeString(value);
            return;
        }

        // 检查是否需要脱敏
        if (!shouldDesensitize("mobile")) {
            gen.writeString(value);
            return;
        }

        // 手机号脱敏：前3位 + **** + 后4位
        int length = value.length();
        if (length < 7) {
            gen.writeString(StringUtils.repeat("*", length));
        } else {
            String masked = value.substring(0, 3) + StringUtils.repeat("*", length - 7) + value.substring(length - 4);
            gen.writeString(masked);
        }
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
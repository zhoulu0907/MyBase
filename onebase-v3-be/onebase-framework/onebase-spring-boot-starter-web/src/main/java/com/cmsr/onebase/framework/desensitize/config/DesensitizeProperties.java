package com.cmsr.onebase.framework.desensitize.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 脱敏配置属性
 *
 * 通过配置文件控制脱敏字段，优先级高于数据库配置
 * 配置项：onebase.desensitize.fields
 * 示例：onebase.desensitize.fields=mobile,email,nickname,username
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "onebase.desensitize")
public class DesensitizeProperties {

    /**
     * 需要脱敏的字段列表（逗号分隔）
     * 可选值：mobile, email, nickname, username
     * 如果配置了此值，将优先使用，不再查询数据库配置
     */
    private String fields;

    /**
     * 是否启用配置文件优先模式
     * true: 优先使用配置文件的fields配置
     * false: 使用数据库配置
     * 默认为 true
     */
    private boolean configFirst = true;

    /**
     * 获取需要脱敏的字段集合
     *
     * @return 脱敏字段集合
     */
    public Set<String> getDesensitizeFields() {
        if (fields == null || fields.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(fields.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    /**
     * 判断是否启用配置文件优先模式且有配置值
     *
     * @return true 表示使用配置文件配置
     */
    public boolean useConfigFirst() {
        return configFirst && fields != null && !fields.trim().isEmpty();
    }
}
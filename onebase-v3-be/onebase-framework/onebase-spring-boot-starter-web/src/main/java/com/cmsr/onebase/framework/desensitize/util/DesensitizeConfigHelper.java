package com.cmsr.onebase.framework.desensitize.util;

import com.cmsr.onebase.framework.common.biz.security.SecurityConfigApi;
import com.cmsr.onebase.framework.desensitize.config.DesensitizeProperties;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 脱敏配置工具类
 *
 * 统一处理脱敏配置的获取逻辑：
 * 1. 优先使用配置文件配置（onebase.desensitize.fields）
 * 2. 如果配置文件未配置，则使用数据库配置
 */
@Component
public class DesensitizeConfigHelper {

    @Resource
    private DesensitizeProperties desensitizeProperties;

    @Resource
    private SecurityConfigApi securityConfigApi;

    /**
     * 获取需要脱敏的字段集合
     *
     * @return 需要脱敏的字段集合
     */
    public Set<String> getDesensitizedFields() {
        // 优先使用配置文件配置
        if (desensitizeProperties.useConfigFirst()) {
            return desensitizeProperties.getDesensitizeFields();
        }
        // 使用数据库配置
        return securityConfigApi.getTenantDesensitizedFieldValues();
    }

    /**
     * 判断指定字段是否需要脱敏
     *
     * @param fieldName 字段名称
     * @return true 表示需要脱敏
     */
    public boolean shouldDesensitize(String fieldName) {
        return getDesensitizedFields().contains(fieldName);
    }
}
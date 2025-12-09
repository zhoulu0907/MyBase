package com.cmsr.onebase.plugin.runtime.context;

import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.context.PluginContextHolder;
import com.cmsr.onebase.plugin.service.CacheService;
import com.cmsr.onebase.plugin.service.DataService;
import com.cmsr.onebase.plugin.service.FileService;
import com.cmsr.onebase.plugin.service.UserService;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 默认插件上下文实现
 * <p>
 * 基于Spring ApplicationContext提供服务获取能力，
 * 从当前线程上下文获取身份信息。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public class DefaultPluginContext implements PluginContext {

    private final String pluginId;
    private final String tenantId;
    private final String userId;
    private final String traceId;
    private final Map<String, Object> attributes;
    private final Map<String, Object> requestParams;
    private final Map<String, Object> requestBody;
    private final Map<String, String> headers;
    private final ApplicationContext applicationContext;

    public DefaultPluginContext(String pluginId, String tenantId, String userId, String traceId,
                                 Map<String, Object> attributes, ApplicationContext applicationContext) {
        this.pluginId = pluginId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.traceId = traceId;
        this.attributes = attributes != null ? attributes : new HashMap<>();
        this.requestParams = new HashMap<>();
        this.requestBody = new HashMap<>();
        this.headers = new HashMap<>();
        this.applicationContext = applicationContext;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getTraceId() {
        return traceId;
    }

    @Override
    public Map<String, Object> getRequestParams() {
        return requestParams;
    }

    @Override
    public Map<String, Object> getRequestBody() {
        return requestBody;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        if (applicationContext != null && applicationContext.getBeanNamesForType(serviceClass).length > 0) {
            return applicationContext.getBean(serviceClass);
        }
        return null;
    }

    /**
     * 设置请求参数
     *
     * @param params 参数Map
     */
    public void setRequestParams(Map<String, Object> params) {
        if (params != null) {
            this.requestParams.putAll(params);
        }
    }

    /**
     * 设置请求体
     *
     * @param body 请求体Map
     */
    public void setRequestBody(Map<String, Object> body) {
        if (body != null) {
            this.requestBody.putAll(body);
        }
    }

    /**
     * 设置请求头
     *
     * @param name  头名称
     * @param value 头值
     */
    public void setHeader(String name, String value) {
        this.headers.put(name, value);
    }

    /**
     * 构建器
     */
    public static class Builder {
        private String pluginId;
        private String tenantId;
        private String userId;
        private String traceId;
        private Map<String, Object> attributes;
        private ApplicationContext applicationContext;

        public Builder pluginId(String pluginId) {
            this.pluginId = pluginId;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder tenantId(Long tenantId) {
            this.tenantId = tenantId != null ? tenantId.toString() : null;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId != null ? userId.toString() : null;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder applicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }

        public DefaultPluginContext build() {
            return new DefaultPluginContext(pluginId, tenantId, userId, traceId, attributes, applicationContext);
        }

        /**
         * 构建并设置到当前线程
         *
         * @return 插件上下文
         */
        public DefaultPluginContext buildAndSet() {
            DefaultPluginContext context = build();
            PluginContextHolder.set(context);
            return context;
        }
    }

    /**
     * 创建构建器
     *
     * @return 构建器
     */
    public static Builder builder() {
        return new Builder();
    }
}

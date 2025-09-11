package com.cmsr.onebase.module.metadata.dal.database;

import com.cmsr.onebase.module.metadata.dal.dataobject.datasource.MetadataDatasourceDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.anyline.data.jdbc.util.DataSourceUtil;
import org.anyline.proxy.ServiceProxy;
import org.anyline.service.AnylineService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 临时数据源服务，用于动态创建和管理AnylineService实例
 * 该服务主要用于元数据模块中对不同数据源的动态访问
 *
 * @author bty418
 * @date 2025-01-25
 */
@Component
@Slf4j
public class TemporaryDatasourceService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 用于缓存已创建的临时服务，避免重复创建
     */
    private final Map<String, AnylineService<?>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 根据数据源配置对象创建临时的AnylineService
     *
     * @param datasource 数据源配置对象
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(MetadataDatasourceDO datasource) {
        // 从数据源配置中获取连接参数
        Map<String, Object> config = parseConfigString(datasource.getConfig());
        config.put("datasourceType", datasource.getDatasourceType());

        // 使用Map版本创建临时服务
        return createTemporaryService(config);
    }

    /**
     * 根据数据源配置Map创建临时的AnylineService
     *
     * @param config 数据源配置参数
     * @return AnylineService实例
     */
    public AnylineService<?> createTemporaryService(Map<String, Object> config) {
        try {
            // 生成缓存键，基于配置内容
            String cacheKey = generateCacheKey(config);

            // 检查缓存中是否已存在
            if (serviceCache.containsKey(cacheKey)) {
                AnylineService<?> cachedService = serviceCache.get(cacheKey);
                if (cachedService != null) {
                    log.debug("使用缓存的临时数据源服务，缓存键: {}", cacheKey);
                    return cachedService;
                }
            }

            // 创建临时数据源
            DataSource tempDataSource = DataSourceUtil.build(config);
            if (tempDataSource == null) {
                throw new IllegalArgumentException("无法创建临时数据源，配置可能不正确: " + config);
            }

            // 使用ServiceProxy创建临时的AnylineService
            AnylineService<?> tempService = ServiceProxy.temporary(tempDataSource);
            if (tempService == null) {
                throw new IllegalStateException("无法创建临时AnylineService");
            }

            // 缓存服务实例
            serviceCache.put(cacheKey, tempService);
            log.info("成功创建临时数据源服务，缓存键: {}", cacheKey);

            return tempService;

        } catch (Exception e) {
            log.error("创建临时数据源服务失败，配置: {}", config, e);
            throw new RuntimeException("创建临时数据源服务失败", e);
        }
    }

    /**
     * 清理指定的缓存服务
     *
     * @param config 数据源配置
     */
    public void clearCachedService(Map<String, Object> config) {
        String cacheKey = generateCacheKey(config);
        serviceCache.remove(cacheKey);
        log.info("清理缓存的临时数据源服务，缓存键: {}", cacheKey);
    }

    /**
     * 清理所有缓存的服务
     */
    public void clearAllCachedServices() {
        serviceCache.clear();
        log.info("清理所有缓存的临时数据源服务");
    }

    /**
     * 解析配置字符串为Map
     *
     * @param configString JSON格式的配置字符串
     * @return 配置Map
     */
    private Map<String, Object> parseConfigString(String configString) {
        if (!StringUtils.hasText(configString)) {
            throw new IllegalArgumentException("数据源配置字符串不能为空");
        }
        
        try {
            return objectMapper.readValue(configString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.error("解析数据源配置字符串失败: {}", configString, e);
            throw new RuntimeException("解析数据源配置失败", e);
        }
    }

    /**
     * 生成缓存键
     *
     * @param config 数据源配置
     * @return 缓存键
     */
    private String generateCacheKey(Map<String, Object> config) {
        // 使用主要配置项生成缓存键
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(config.get("url")).append("_")
                  .append(config.get("username")).append("_")
                  .append(config.get("datasourceType"));
        return keyBuilder.toString();
    }

    /**
     * 测试数据源连接
     *
     * @param config 数据源配置
     * @return 是否连接成功
     */
    public boolean testConnection(Map<String, Object> config) {
        try {
            AnylineService<?> testService = createTemporaryService(config);
            // 简单的连接测试
            return testService != null;
        } catch (Exception e) {
            log.warn("测试数据源连接失败: {}", config, e);
            return false;
        }
    }

    /**
     * 获取缓存统计信息
     *
     * @return 缓存统计信息
     */
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("cachedServicesCount", serviceCache.size());
        stats.put("cacheKeys", serviceCache.keySet());
        return stats;
    }
}
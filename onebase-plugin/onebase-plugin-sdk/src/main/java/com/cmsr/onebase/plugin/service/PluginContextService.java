package com.cmsr.onebase.plugin.service;

import java.util.Map;

/**
 * 插件配置查询服务接口
 * <p>
 * 提供插件动态获取配置参数的能力。
 * 插件开发者可通过此接口获取由宿主系统管理的配置。
 * </p>
 * <p>
 * 使用场景示例：
 * <ul>
 * <li>OCR 插件获取服务商配置（百度/阿里）</li>
 * <li>获取 API Key、Endpoint 等敏感参数</li>
 * <li>获取业务相关的动态配置</li>
 * </ul>
 * </p>
 * <p>
 * <b>实现说明：</b>实现类只需实现 {@link #getConfig(String, String)} 方法，
 * 其他方法都有默认实现，会自动基于 getConfig 方法获取数据。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-01-05
 */
public interface PluginContextService {

    /**
     * Get current tenant ID
     *
     * @return tenant ID
     */
    Long getTenantId();

    /**
     * 获取指定插件的全部配置
     * <p>
     * <b>核心方法</b>：实现类必须实现此方法，其他方法会基于此方法提供默认实现。
     * </p>
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @return 配置键值对，如果插件无配置则返回空 Map（不要返回 null）
     */
    Map<String, Object> getConfig(String pluginId, String version);

    /**
     * 获取指定配置项的值
     * <p>
     * 默认实现：从 {@link #getConfig(String, String)} 返回的 Map 中获取指定 key 的值。
     * </p>
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @param key      配置键
     * @return 配置值，如果不存在则返回 null
     */
    default Object getConfigValue(String pluginId, String version, String key) {
        Map<String, Object> config = getConfig(pluginId, version);
        return config != null ? config.get(key) : null;
    }

    /**
     * 获取指定配置项的值（带默认值）
     * <p>
     * 默认实现：从 {@link #getConfig(String, String)} 返回的 Map 中获取指定 key 的值，
     * 如果不存在或类型转换失败则返回默认值。
     * </p>
     *
     * @param pluginId     插件ID
     * @param version      插件版本
     * @param key          配置键
     * @param defaultValue 默认值
     * @param <T>          值类型
     * @return 配置值，如果不存在则返回默认值
     */
    @SuppressWarnings("unchecked")
    default <T> T getConfigValue(String pluginId, String version, String key, T defaultValue) {
        Object value = getConfigValue(pluginId, version, key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return (T) value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    /**
     * 检查指定插件是否有配置
     * <p>
     * 默认实现：检查 {@link #getConfig(String, String)} 返回的 Map 是否非空。
     * </p>
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @return true 表示有配置
     */
    default boolean hasConfig(String pluginId, String version) {
        Map<String, Object> config = getConfig(pluginId, version);
        return config != null && !config.isEmpty();
    }

    /**
     * 检查指定配置项是否存在
     * <p>
     * 默认实现：检查 {@link #getConfig(String, String)} 返回的 Map 中是否包含指定 key。
     * </p>
     *
     * @param pluginId 插件ID
     * @param version  插件版本
     * @param key      配置键
     * @return true 表示配置项存在
     */
    default boolean hasConfigKey(String pluginId, String version, String key) {
        Map<String, Object> config = getConfig(pluginId, version);
        return config != null && config.containsKey(key);
    }
}

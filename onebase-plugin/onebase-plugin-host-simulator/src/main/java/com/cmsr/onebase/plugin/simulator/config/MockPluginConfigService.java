package com.cmsr.onebase.plugin.simulator.config;

import com.cmsr.onebase.plugin.service.PluginConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模拟插件配置服务
 * <p>
 * 为插件开发者提供模拟的配置服务，支持：
 * <ul>
 * <li>从 YAML 文件加载配置</li>
 * <li>运行时动态更新配置（便于测试）</li>
 * <li>按插件ID和版本隔离配置</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-01-05
 */
@Service
public class MockPluginConfigService implements PluginConfigService {

    private static final Logger log = LoggerFactory.getLogger(MockPluginConfigService.class);

    /**
     * 插件配置存储：pluginKey (pluginId:version) -> config map
     * 注意：模拟器中暂时忽略版本，使用 pluginId 作为 key
     */
    private final Map<String, Map<String, Object>> pluginConfigs = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        loadConfigFromFile();
    }

    /**
     * 从配置文件加载插件配置
     * <p>
     * 加载优先级：
     * 1. 外部文件：JAR 包同目录的 plugin-config.yml
     * 2. 内部文件：classpath:plugin-config.yml（默认模板）
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void loadConfigFromFile() {
        Resource configResource = resolveConfigResource();

        if (configResource == null || !configResource.exists()) {
            log.warn("[MockPluginConfigService] 配置文件不存在，使用空配置");
            return;
        }

        try (InputStream inputStream = configResource.getInputStream()) {
            log.info("[MockPluginConfigService] 从 {} 加载配置", configResource.getDescription());

            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(inputStream);

            if (root == null) {
                log.info("[MockPluginConfigService] 配置文件为空");
                return;
            }

            // 期望格式: plugins: { pluginId: { key: value, ... }, ... }
            Object pluginsObj = root.get("plugins");
            if (pluginsObj instanceof Map) {
                Map<String, Object> plugins = (Map<String, Object>) pluginsObj;
                for (Map.Entry<String, Object> entry : plugins.entrySet()) {
                    String pluginId = entry.getKey();
                    Object configObj = entry.getValue();
                    if (configObj instanceof Map) {
                        Map<String, Object> config = new ConcurrentHashMap<>((Map<String, Object>) configObj);
                        pluginConfigs.put(pluginId, config);
                        log.info("[MockPluginConfigService] 已加载插件 [{}] 配置，共 {} 项",
                                pluginId, config.size());
                    }
                }
            }

            log.info("[MockPluginConfigService] 配置加载完成，共 {} 个插件", pluginConfigs.size());
        } catch (IOException e) {
            log.error("[MockPluginConfigService] 加载配置文件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 解析配置文件资源
     * <p>
     * 优先级：
     * 1. 外部文件：./plugin-config.yml（JAR 包同目录）
     * 2. 内部文件：classpath:plugin-config.yml（JAR 包内默认模板）
     * </p>
     */
    private Resource resolveConfigResource() {
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        // 1. 优先加载外部文件（JAR 包同目录）
        Resource externalFile = resourceLoader.getResource("file:./plugin-config.yml");
        if (externalFile.exists()) {
            log.info("[MockPluginConfigService] 使用外部配置文件: ./plugin-config.yml");
            return externalFile;
        }

        // 2. 降级到 JAR 包内部文件（默认模板）
        Resource classpathFile = resourceLoader.getResource("classpath:plugin-config.yml");
        if (classpathFile.exists()) {
            log.info("[MockPluginConfigService] jar同目录下不存在plugin-config.yml，使用内置配置文件: classpath:plugin-config.yml");
            return classpathFile;
        }

        log.error("[MockPluginConfigService] 未找到配置文件（外部: ./plugin-config.yml, 内部: classpath:plugin-config.yml）");
        return null;
    }

    @Override
    public Map<String, Object> getConfig(String pluginId, String version) {
        // 模拟器中暂时忽略版本，直接使用 pluginId
        Map<String, Object> config = pluginConfigs.get(pluginId);
        if (config == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(config);
    }

    // ==================== 动态配置管理（用于测试） ====================

    /**
     * 设置插件配置（用于测试和动态更新）
     *
     * @param pluginId 插件ID
     * @param config   配置Map
     */
    public void setConfig(String pluginId, Map<String, Object> config) {
        pluginConfigs.put(pluginId, new ConcurrentHashMap<>(config));
        log.info("[MockPluginConfigService] 已更新插件 [{}] 配置，共 {} 项", pluginId, config.size());
    }

    /**
     * 设置单个配置项
     *
     * @param pluginId 插件ID
     * @param key      配置键
     * @param value    配置值
     */
    public void setConfigValue(String pluginId, String key, Object value) {
        pluginConfigs.computeIfAbsent(pluginId, k -> new ConcurrentHashMap<>())
                .put(key, value);
        log.info("[MockPluginConfigService] 已设置配置: pluginId={}, key={}, value={}",
                pluginId, key, value);
    }

    /**
     * 删除插件配置
     *
     * @param pluginId 插件ID
     */
    public void removeConfig(String pluginId) {
        pluginConfigs.remove(pluginId);
        log.info("[MockPluginConfigService] 已删除插件 [{}] 配置", pluginId);
    }

    /**
     * 删除单个配置项
     *
     * @param pluginId 插件ID
     * @param key      配置键
     */
    public void removeConfigValue(String pluginId, String key) {
        Map<String, Object> config = pluginConfigs.get(pluginId);
        if (config != null) {
            config.remove(key);
            log.info("[MockPluginConfigService] 已删除配置项: pluginId={}, key={}", pluginId, key);
        }
    }

    /**
     * 获取所有插件配置（用于调试）
     *
     * @return 所有插件配置
     */
    public Map<String, Map<String, Object>> getAllConfigs() {
        return Collections.unmodifiableMap(pluginConfigs);
    }

    /**
     * 重新加载配置文件
     */
    public void reloadConfig() {
        pluginConfigs.clear();
        loadConfigFromFile();
        log.info("[MockPluginConfigService] 配置已重新加载");
    }
}

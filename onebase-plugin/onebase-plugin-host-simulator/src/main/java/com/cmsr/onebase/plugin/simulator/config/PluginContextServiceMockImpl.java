package com.cmsr.onebase.plugin.simulator.config;

import com.cmsr.onebase.plugin.service.PluginContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class PluginContextServiceMockImpl implements PluginContextService {

    private static final Logger log = LoggerFactory.getLogger(PluginContextServiceMockImpl.class);

    /**
     * 插件配置存储：pluginKey (pluginId:version) -> config map
     * 注意：模拟器中暂时忽略版本，使用 pluginId 作为 key
     */
    private final Map<String, Map<String, Object>> pluginConfigs = new ConcurrentHashMap<>();

    /**
     * 配置文件监听器
     */
    private ContextFileWatcher configFileWatcher;

    /**
     * Mock tenant ID (will be loaded from YAML later)
     */
    private Long mockTenantId = 1L;

    /**
     * Mock application ID (will be loaded from YAML later)
     */
    private Long mockApplicationId = 100L;

    @Override
    public Long getTenantId() {
        log.debug("模拟租户ID值为: {}", mockTenantId);
        return mockTenantId;
    }

    @Override
    public Long getApplicationId() {
        log.debug("模拟应用ID值为: {}", mockApplicationId);
        return mockApplicationId;
    }

    @PostConstruct
    public void init() {
        loadConfigFromFile();
        startConfigFileWatcher();
    }

    /**
     * 销毁时停止配置文件监听
     */
    @PreDestroy
    public void destroy() {
        stopConfigFileWatcher();
    }

    /**
     * 从配置文件加载插件配置
     * <p>
     * 加载优先级：
     * 1. 外部文件：JAR 包同目录的 plugin-context.yml
     * 2. 内部文件：classpath:plugin-context.yml（默认模板）
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void loadConfigFromFile() {
        Resource configResource = resolveConfigResource();

        if (configResource == null || !configResource.exists()) {
            log.warn("配置文件不存在，使用空配置");
            return;
        }

        try (InputStream inputStream = configResource.getInputStream()) {
            log.info("从 {} 加载配置", configResource.getDescription());

            Yaml yaml = new Yaml();
            Map<String, Object> root = yaml.load(inputStream);

            if (root == null) {
                log.info("配置文件为空");
                return;
            }

            // 1. 解析 Tenant 配置
            Object tenantObj = root.get("tenant");
            if (tenantObj instanceof Map) {
                Map<String, Object> tenantConfig = (Map<String, Object>) tenantObj;

                // 解析 tenant-id
                Object tenantIdObj = tenantConfig.get("tenant-id");
                if (tenantIdObj instanceof Number) {
                    this.mockTenantId = ((Number) tenantIdObj).longValue();
                    log.info("已加载模拟租户 ID: {}", this.mockTenantId);
                }

                // 解析 application-id
                Object applicationIdObj = tenantConfig.get("application-id");
                if (applicationIdObj instanceof Number) {
                    this.mockApplicationId = ((Number) applicationIdObj).longValue();
                    log.info("已加载模拟应用 ID: {}", this.mockApplicationId);
                }
            }

            // 2. 解析插件配置
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
                        log.info("已加载插件 [{}] 配置，共 {} 项",
                                pluginId, config.size());
                    }
                }
            }

            log.info("配置加载完成，共 {} 个插件", pluginConfigs.size());
        } catch (IOException e) {
            log.error("加载配置文件失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 解析配置文件资源
     * <p>
     * 优先级：
     * 1. 外部文件：./plugin-context.yml（JAR 包同目录）
     * 2. 内部文件：classpath:plugin-context.yml（JAR 包内默认模板）
     * </p>
     */
    private Resource resolveConfigResource() {
        ResourceLoader resourceLoader = getResourceLoader();

        // 1. 优先加载外部文件(JAR 包同目录)
        try {
            // 使用 ApplicationHome 自动定位应用根目录(兼容 IDE 和 JAR 模式)
            File homeDir = getApplicationHomeDir();
            File configFile = null; // 初始化 configFile 修复编译错误

            // IDE 模式特殊处理: 如果定位到 target/classes,只查找 target/plugin-context.yml(上一级)
            // 注意: 不应该使用 classes 目录下的文件,那是内部配置(classpath),应该走降级逻辑加载
            if ("classes".equals(homeDir.getName())) {
                File targetDir = homeDir.getParentFile();
                if (targetDir != null) {
                    File targetConfigFile = new File(targetDir, "plugin-context.yml");
                    if (targetConfigFile.exists()) {
                        configFile = targetConfigFile;
                        log.debug("IDE模式: 发现外部配置文件 {}", configFile.getAbsolutePath());
                    } else {
                        log.info("IDE模式: 外部配置文件不存在: {}, 将使用内置配置", targetConfigFile.getAbsolutePath());
                    }
                }
            } else {
                // JAR 模式: 直接使用 homeDir 下的配置文件
                configFile = new File(homeDir, "plugin-context.yml");
                if (!configFile.exists()) {
                    log.info("JAR模式: 外部配置文件不存在: {}, 将使用内置配置", configFile.getAbsolutePath());
                }
            }

            if (configFile != null && configFile.exists()) {
                Resource externalFile = resourceLoader.getResource("file:" + configFile.getAbsolutePath());
                log.info("使用外部配置文件: {}", configFile.getAbsolutePath());
                return externalFile;
            }
        } catch (Exception e) {
            log.error("无法定位外部配置: {}", e.getMessage());
        }

        // 2. 降级到 JAR 包内部文件(默认模板)
        Resource classpathFile = resourceLoader.getResource("classpath:plugin-context.yml");
        if (classpathFile.exists()) {
            log.info("jar同目录下不存在plugin-context.yml，使用内置配置文件: classpath:plugin-context.yml");
            return classpathFile;
        }

        log.error("未找到配置文件（外部: ./plugin-context.yml, 内部: classpath:plugin-context.yml）");
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
        log.info("已更新插件 [{}] 配置，共 {} 项", pluginId, config.size());
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
        log.info("已设置配置: pluginId={}, key={}, value={}",
                pluginId, key, value);
    }

    /**
     * 删除插件配置
     *
     * @param pluginId 插件ID
     */
    public void removeConfig(String pluginId) {
        pluginConfigs.remove(pluginId);
        log.info("已删除插件 [{}] 配置", pluginId);
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
            log.info("已删除配置项: pluginId={}, key={}", pluginId, key);
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
        log.info("配置已重新加载");
    }

    /**
     * 获取应用主目录(Protected 方法便于测试 Mock)
     */
    protected File getApplicationHomeDir() {
        return new ApplicationHome(getClass()).getDir();
    }

    /**
     * 获取资源加载器(Protected 方法便于测试 Mock)
     */
    protected ResourceLoader getResourceLoader() {
        return new DefaultResourceLoader();
    }

    /**
     * 启动配置文件监听器
     */
    private void startConfigFileWatcher() {
        Path configPath = getConfigFilePath();
        if (configPath == null) {
            log.info("未找到外部配置文件，不启动热加载监听");
            return;
        }

        try {
            configFileWatcher = new ContextFileWatcher(configPath, this::reloadConfig);
            configFileWatcher.start();
            log.info("配置文件热加载已启用");
        } catch (Exception e) {
            log.error("启动配置文件监听失败", e);
        }
    }

    /**
     * 停止配置文件监听器
     */
    private void stopConfigFileWatcher() {
        if (configFileWatcher != null) {
            configFileWatcher.stop();
            configFileWatcher = null;
        }
    }

    /**
     * 获取配置文件路径（仅外部文件）
     * <p>
     * 只有外部配置文件才支持热加载，classpath 内的文件无法监控
     * </p>
     */
    private Path getConfigFilePath() {
        try {
            File homeDir = getApplicationHomeDir();
            File configFile = null;

            // IDE 模式特殊处理
            if ("classes".equals(homeDir.getName())) {
                File targetDir = homeDir.getParentFile();
                if (targetDir != null) {
                    File targetConfigFile = new File(targetDir, "plugin-context.yml");
                    if (targetConfigFile.exists()) {
                        configFile = targetConfigFile;
                    }
                }
            } else {
                // JAR 模式
                configFile = new File(homeDir, "plugin-context.yml");
                if (!configFile.exists()) {
                    configFile = null;
                }
            }

            if (configFile != null && configFile.exists()) {
                return Paths.get(configFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("获取配置文件路径失败", e);
        }

        return null;
    }
}

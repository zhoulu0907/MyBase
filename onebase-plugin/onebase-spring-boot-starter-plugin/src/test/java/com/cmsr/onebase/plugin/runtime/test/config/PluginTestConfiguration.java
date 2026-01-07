package com.cmsr.onebase.plugin.runtime.test.config;

import com.cmsr.onebase.plugin.service.PluginConfigQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试配置 - 提供 MockPluginConfigService
 * <p>
 * 为所有测试提供 PluginConfigService 实现，
 * 避免 ConfigDemoController 注入失败
 * </p>
 *
 * @author chengyuansen
 * @date 2026-01-07
 */
@TestConfiguration
public class PluginTestConfiguration {

    private static final Logger log = LoggerFactory.getLogger(PluginTestConfiguration.class);

    @Bean
    public PluginConfigQueryService pluginConfigService() {
        return new MockPluginConfigServiceForTest();
    }

    /**
     * 测试用的 Mock PluginConfigQueryService
     */
    static class MockPluginConfigServiceForTest implements PluginConfigQueryService {

        private final Map<String, Map<String, Object>> pluginConfigs = new ConcurrentHashMap<>();
        private final ResourceLoader resourceLoader = new DefaultResourceLoader();

        public MockPluginConfigServiceForTest() {
            loadConfigFromFile();
        }

        private void loadConfigFromFile() {
            try {
                Resource resource = resourceLoader.getResource("classpath:plugin-config.yml");
                if (!resource.exists()) {
                    log.warn("plugin-config.yml 不存在，使用空配置");
                    return;
                }

                try (InputStream is = resource.getInputStream()) {
                    Yaml yaml = new Yaml();
                    Map<String, Object> yamlData = yaml.load(is);

                    if (yamlData != null && yamlData.containsKey("plugins")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Map<String, Object>> plugins = (Map<String, Map<String, Object>>) yamlData
                                .get("plugins");

                        plugins.forEach((pluginId, config) -> {
                            pluginConfigs.put(pluginId, new ConcurrentHashMap<>(config));
                            log.info("已加载插件 {} 的配置，包含 {} 个配置项", pluginId, config.size());
                        });
                    }
                }
            } catch (IOException e) {
                log.error("加载插件配置文件失败", e);
            }
        }

        @Override
        public Map<String, Object> getConfig(String pluginId, String version) {
            return pluginConfigs.getOrDefault(pluginId, Collections.emptyMap());
        }

        @Override
        public Object getConfigValue(String pluginId, String version, String key) {
            Map<String, Object> config = getConfig(pluginId, version);
            return config.get(key);
        }

        @Override
        public Object getConfigValue(String pluginId, String version, String key, Object defaultValue) {
            Object value = getConfigValue(pluginId, version, key);
            return value != null ? value : defaultValue;
        }

        @Override
        public boolean hasConfigKey(String pluginId, String version, String key) {
            Map<String, Object> config = getConfig(pluginId, version);
            return config.containsKey(key);
        }
    }
}

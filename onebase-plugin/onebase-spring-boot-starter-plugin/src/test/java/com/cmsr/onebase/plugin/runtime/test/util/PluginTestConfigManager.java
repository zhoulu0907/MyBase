package com.cmsr.onebase.plugin.runtime.test.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 插件测试配置管理器
 * <p>
 * 负责动态生成不同模式和配置组合的 application.yml 文件，
 * 支持测试用例快速切换配置。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-25
 */
public class PluginTestConfigManager {

    private static final Logger log = LoggerFactory.getLogger(PluginTestConfigManager.class);

    private static final String TEST_RESOURCES_PATH = "src/test/resources";
    private static final String CONFIG_FILE_NAME = "application-test.yml";

    /**
     * 配置模式枚举
     */
    public enum PluginMode {
        DEV("dev"),
        STAGING("staging"),
        PROD("prod");

        private final String value;

        PluginMode(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 配置构建器
     */
    public static class ConfigBuilder {
        private boolean enabled = true;
        private PluginMode mode = PluginMode.DEV;
        private boolean autoLoad = true;
        private boolean autoStart = true;
        private List<String> devClassPaths = List.of();
        private String pluginsDir = "plugins";
        private Map<String, Object> additionalProps = new HashMap<>();

        public ConfigBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public ConfigBuilder mode(PluginMode mode) {
            this.mode = mode;
            return this;
        }

        public ConfigBuilder autoLoad(boolean autoLoad) {
            this.autoLoad = autoLoad;
            return this;
        }

        public ConfigBuilder autoStart(boolean autoStart) {
            this.autoStart = autoStart;
            return this;
        }

        public ConfigBuilder devClassPaths(List<String> devClassPaths) {
            this.devClassPaths = devClassPaths;
            return this;
        }

        public ConfigBuilder pluginsDir(String pluginsDir) {
            this.pluginsDir = pluginsDir;
            return this;
        }

        public ConfigBuilder additionalProperty(String key, Object value) {
            this.additionalProps.put(key, value);
            return this;
        }

        /**
         * 生成配置文件内容
         */
        public String build() {
            StringBuilder yml = new StringBuilder();

            // Spring Boot 基础配置
            yml.append("spring:\n");
            yml.append("  application:\n");
            yml.append("    name: onebase-plugin-test\n");
            yml.append("  main:\n");
            yml.append("    allow-bean-definition-overriding: true\n");
            yml.append("\n");

            // 插件配置
            yml.append("onebase:\n");
            yml.append("  plugin:\n");
            yml.append("    enabled: ").append(enabled).append("\n");
            yml.append("    mode: ").append(mode.getValue()).append("\n");
            yml.append("    auto-load: ").append(autoLoad).append("\n");
            yml.append("    auto-start: ").append(autoStart).append("\n");

            // dev-class-paths 配置
            if (devClassPaths != null && !devClassPaths.isEmpty()) {
                yml.append("    dev-class-paths:\n");
                for (String path : devClassPaths) {
                    yml.append("      - ").append(path).append("\n");
                }
            }

            // plugins-dir 配置
            yml.append("    plugins-dir: ").append(pluginsDir).append("\n");

            // 附加配置
            if (!additionalProps.isEmpty()) {
                yml.append("\n# Additional properties\n");
                additionalProps.forEach((key, value) -> {
                    yml.append(key).append(": ").append(value).append("\n");
                });
            }

            return yml.toString();
        }

        /**
         * 写入配置文件
         */
        public void writeToFile(String targetPath) throws IOException {
            String content = build();
            File file = new File(targetPath);

            // 确保父目录存在
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }

            log.info("配置文件已写入: {}", targetPath);
            log.debug("配置内容:\n{}", content);
        }
    }

    /**
     * 创建配置构建器
     */
    public static ConfigBuilder builder() {
        return new ConfigBuilder();
    }

    /**
     * 预定义配置：DEV 模式全启用
     */
    public static ConfigBuilder devModeFullEnabled() {
        return builder()
                .enabled(true)
                .mode(PluginMode.DEV)
                .autoLoad(true)
                .autoStart(true)
                .devClassPaths(List.of(
                        "../onebase-plugin-demo/plugin-demo-hello/target/classes"));
    }

    /**
     * 预定义配置：DEV 模式禁用
     */
    public static ConfigBuilder devModeDisabled() {
        return builder()
                .enabled(false)
                .mode(PluginMode.DEV);
    }

    /**
     * 预定义配置：STAGING 模式全启用
     */
    public static ConfigBuilder stagingModeFullEnabled() {
        return builder()
                .enabled(true)
                .mode(PluginMode.STAGING)
                .autoLoad(true)
                .autoStart(true)
                .pluginsDir("plugins");
    }

    /**
     * 预定义配置：STAGING 模式禁用
     */
    public static ConfigBuilder stagingModeDisabled() {
        return builder()
                .enabled(false)
                .mode(PluginMode.STAGING);
    }

    /**
     * 预定义配置：PROD 模式全启用
     */
    public static ConfigBuilder prodModeFullEnabled() {
        return builder()
                .enabled(true)
                .mode(PluginMode.PROD)
                .autoLoad(true)
                .autoStart(true)
                .pluginsDir("plugins");
    }

    /**
     * 预定义配置：PROD 模式禁用
     */
    public static ConfigBuilder prodModeDisabled() {
        return builder()
                .enabled(false)
                .mode(PluginMode.PROD);
    }

    /**
     * 清理测试配置文件
     */
    public static void cleanupTestConfig(String configPath) {
        try {
            Path path = Paths.get(configPath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("已清理测试配置文件: {}", configPath);
            }
        } catch (IOException e) {
            log.warn("清理测试配置文件失败: {}", configPath, e);
        }
    }
}

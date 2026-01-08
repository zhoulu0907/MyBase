package com.cmsr.onebase.plugin.simulator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MockPluginConfigServiceTest {

    @TempDir
    Path tempDir;

    private TestableMockPluginConfigService configService;

    @BeforeEach
    void setUp() {
        configService = new TestableMockPluginConfigService();
    }

    // --------------------------------------------------------------------------------------
    // IDE 模式测试场景
    // --------------------------------------------------------------------------------------

    /**
     * 场景 1: IDE 模式：外部配置文件不存在，内部配置文件存在
     * 预期：加载内部配置文件 (Fall back to classpath)
     */
    @Test
    void scene1_IdeMode_ExternalMissing_InternalExists() {
        Path targetDir = tempDir.resolve("target");
        Path classesDir = targetDir.resolve("classes");
        // classes 存在但无文件
        configService.setAppHomeDir(classesDir.toFile());

        // 模拟 ResourceLoader 能找到内部配置
        // 这里的逻辑是: 外部找不到 -> 走到降级逻辑 ->
        // resourceLoader.getResource("classpath:plugin-config.yml")
        // 由于测试环境真实 classpath 下可能没有 plugin-config.yml (除非我们在 src/test/resources 加一个)
        // 或者我们可以 mock resourceLoader 让它返回存在的 resource

        // 实际上 src/main/resources 下通常会有 plugin-config.yml，所以 maven compile 后
        // target/classes 下会有
        // 在单元测试中，classpath 会包含 target/classes 和 target/test-classes

        // 为了确保测试独立性，我们 mock resourceLoader
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                if ("classpath:plugin-config.yml".equals(location)) {
                    // 返回一个临时文件作为内部配置
                    try {
                        Path p = tempDir.resolve("internal-config.yml");
                        if (!Files.exists(p)) {
                            Files.writeString(p, "plugins:\n  scene1:\n    key: internal");
                        }
                        return super.getResource("file:" + p.toAbsolutePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return super.getResource(location);
            }
        });

        configService.loadConfigFromFile();

        Map<String, Object> config = configService.getConfig("scene1", "1.0.0");
        assertNotNull(config);
        assertEquals("internal", config.get("key"));
    }

    /**
     * 场景 2: IDE 模式：外部配置文件不存在，内部配置文件也不存在
     * 预期：加载失败，无配置
     */
    @Test
    void scene2_IdeMode_AllMissing() {
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                // 模拟找不到任何配置
                return super.getResource("file:non-existent.yml");
            }
        });

        Path targetDir = tempDir.resolve("target");
        Path classesDir = targetDir.resolve("classes");
        configService.setAppHomeDir(classesDir.toFile());

        configService.loadConfigFromFile();

        assertTrue(configService.getAllConfigs().isEmpty(), "所有配置文件不存在时，配置应为空");
    }

    /**
     * 场景 3: IDE 模式：外部配置文件存在，内部配置文件也存在
     * 预期：优先使用外部配置文件 (target/plugin-config.yml)
     */
    @Test
    void scene3_IdeMode_BothExist() throws IOException {
        Path targetDir = tempDir.resolve("target");
        Path classesDir = targetDir.resolve("classes");
        Files.createDirectories(classesDir);

        // 创建外部配置文件
        Path externalConfig = targetDir.resolve("plugin-config.yml");
        Files.writeString(externalConfig, "plugins:\n  scene3:\n    key: external");

        // Mock 内部配置存在
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                if ("classpath:plugin-config.yml".equals(location)) {
                    try {
                        Path p = tempDir.resolve("internal-config.yml");
                        if (!Files.exists(p))
                            Files.writeString(p, "plugins:\n  scene3:\n    key: internal");
                        return super.getResource("file:" + p.toAbsolutePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return super.getResource(location);
            }
        });

        configService.setAppHomeDir(classesDir.toFile());
        configService.loadConfigFromFile();

        Map<String, Object> config = configService.getConfig("scene3", "1.0.0");
        assertNotNull(config);
        assertEquals("external", config.get("key"), "应优先加载外部配置");
    }

    /**
     * 场景 4: IDE 模式：外部配置文件存在，内部配置文件也不存在
     * 预期：加载外部配置文件
     */
    @Test
    void scene4_IdeMode_ExternalExists_InternalMissing() throws IOException {
        Path targetDir = tempDir.resolve("target");
        Path classesDir = targetDir.resolve("classes");
        Files.createDirectories(classesDir);

        // 创建外部配置文件
        Path externalConfig = targetDir.resolve("plugin-config.yml");
        Files.writeString(externalConfig, "plugins:\n  scene4:\n    key: external");

        // Mock 内部配置缺失
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                if ("classpath:plugin-config.yml".equals(location)) {
                    return super.getResource("file:non-existent.yml");
                }
                return super.getResource(location);
            }
        });

        configService.setAppHomeDir(classesDir.toFile());
        configService.loadConfigFromFile();

        Map<String, Object> config = configService.getConfig("scene4", "1.0.0");
        assertNotNull(config);
        assertEquals("external", config.get("key"));
    }

    // --------------------------------------------------------------------------------------
    // JAR 模式测试场景
    // --------------------------------------------------------------------------------------

    /**
     * 场景 5: JAR模式：jar包同级目录存在 plugin-config.yml
     * 预期：加载同级目录配置文件
     */
    @Test
    void scene5_JarMode_ExternalExists() throws IOException {
        Path externalConfig = tempDir.resolve("plugin-config.yml");
        Files.writeString(externalConfig, "plugins:\n  scene5:\n    key: jar-external");

        configService.setAppHomeDir(tempDir.toFile());
        // JAR模式下 resourceLoader 行为无需特殊 mock，因为优先加载外部
        configService.loadConfigFromFile();

        Map<String, Object> config = configService.getConfig("scene5", "1.0.0");
        assertNotNull(config);
        assertEquals("jar-external", config.get("key"));
    }

    /**
     * 场景 6: JAR模式：jar包同级目录不存在 plugin-config.yml，内置配置文件存在
     * 预期：加载内置配置文件
     */
    @Test
    void scene6_JarMode_ExternalMissing_InternalExists() {
        configService.setAppHomeDir(tempDir.toFile());

        // Mock 内部配置存在
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                if ("classpath:plugin-config.yml".equals(location)) {
                    try {
                        Path p = tempDir.resolve("internal-config.yml");
                        if (!Files.exists(p))
                            Files.writeString(p, "plugins:\n  scene6:\n    key: internal");
                        return super.getResource("file:" + p.toAbsolutePath());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return super.getResource(location);
            }
        });

        configService.loadConfigFromFile();

        Map<String, Object> config = configService.getConfig("scene6", "1.0.0");
        assertNotNull(config);
        assertEquals("internal", config.get("key"));
    }

    /**
     * 场景 7: JAR模式：jar包同级目录不存在 plugin-config.yml，内置配置文件也不存在
     * 预期：无配置
     */
    @Test
    void scene7_JarMode_AllMissing() {
        configService.setAppHomeDir(tempDir.toFile());

        // Mock 内部配置缺失
        configService.setResourceLoader(new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return super.getResource("file:non-existent.yml");
            }
        });

        configService.loadConfigFromFile();

        assertTrue(configService.getAllConfigs().isEmpty());
    }

    // 可测试的子类
    static class TestableMockPluginConfigService extends MockPluginConfigQueryService {
        private File appHomeDir;
        private ResourceLoader resourceLoader = new DefaultResourceLoader();

        public void setAppHomeDir(File appHomeDir) {
            this.appHomeDir = appHomeDir;
        }

        public void setResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
        }

        @Override
        protected File getApplicationHomeDir() {
            return appHomeDir;
        }

        @Override
        protected ResourceLoader getResourceLoader() {
            return resourceLoader;
        }
    }
}

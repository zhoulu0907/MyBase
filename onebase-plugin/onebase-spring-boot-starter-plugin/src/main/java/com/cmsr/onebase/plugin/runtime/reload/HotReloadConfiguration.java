package com.cmsr.onebase.plugin.runtime.reload;

import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.http.PluginControllerRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import jakarta.annotation.PreDestroy;
import java.nio.file.Paths;
import java.util.List;

/**
 * 热重载配置类
 * <p>
 * 仅在 dev 模式下启用，负责初始化和管理热重载功能。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-23
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "onebase.plugin.mode", havingValue = "dev")
public class HotReloadConfiguration {

    private final PluginProperties pluginProperties;
    private final ApplicationContext applicationContext;
    private final PluginControllerRegistrar controllerRegistrar;

    private DevClassPathWatcher watcher;
    private HotReloadManager hotReloadManager;

    public HotReloadConfiguration(PluginProperties pluginProperties,
            ApplicationContext applicationContext,
            PluginControllerRegistrar controllerRegistrar) {
        this.pluginProperties = pluginProperties;
        this.applicationContext = applicationContext;
        this.controllerRegistrar = controllerRegistrar;
    }

    /**
     * 应用启动完成后初始化热重载
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initHotReload() {
        List<String> devClassPaths = pluginProperties.getDevClassPaths();

        if (devClassPaths == null || devClassPaths.isEmpty()) {
            log.warn("dev-class-paths 未配置，热重载功能不可用");
            return;
        }

        try {
            log.info("初始化热重载功能");
            log.info("监听路径: {}", devClassPaths);

            // 使用第一个路径作为 classes root
            // 假设所有 dev-class-paths 都指向同一个项目的不同模块的 target/classes
            String firstPath = devClassPaths.get(0);

            // 创建热重载管理器
            hotReloadManager = new HotReloadManager(
                    applicationContext,
                    controllerRegistrar,
                    Paths.get(firstPath));

            // 创建文件监听器
            watcher = new DevClassPathWatcher(devClassPaths, hotReloadManager);

            // 启动监听
            watcher.start();

            log.info("热重载功能已启用");
        } catch (Exception e) {
            log.error("初始化热重载功能失败", e);
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void shutdown() {
        log.info("关闭热重载功能");

        if (watcher != null) {
            try {
                watcher.stop();
            } catch (Exception e) {
                log.warn("关闭文件监听器时出错: {}", e.getMessage());
            }
        }

        if (hotReloadManager != null) {
            try {
                hotReloadManager.shutdown();
            } catch (Exception e) {
                log.warn("关闭热重载管理器时出错: {}", e.getMessage());
            }
        }
    }
}

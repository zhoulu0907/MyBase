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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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

            // 将所有 devClassPaths 转换为 Path 列表
            List<Path> classesRoots = devClassPaths.stream()
                    .map(Paths::get)
                    .collect(java.util.stream.Collectors.toList());

            // 创建热重载管理器,传入所有路径
            hotReloadManager = new HotReloadManager(
                    applicationContext,
                    controllerRegistrar,
                    classesRoots);

            // 将热重载管理器注入到插件管理器的扫描器中（用于插件级热重载）
            try {
                Object pluginManager = applicationContext.getBean("pluginManager");
                if (pluginManager != null) {
                    pluginManager.getClass()
                            .getMethod("setHotReloadManager", Object.class)
                            .invoke(pluginManager, hotReloadManager);
                    log.info("已将热重载管理器注入到插件管理器");

                    // 【P0 修复】：手动注册已扫描的扩展点
                    // 因为扫描发生在 HotReloadManager 创建之前，需要补充注册
                    try {
                        // 获取扩展点缓存
                        java.lang.reflect.Field cacheField = pluginManager.getClass()
                                .getDeclaredField("extensionCache");
                        cacheField.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Map<Class<?>, List<?>> extensionCache = (Map<Class<?>, List<?>>) cacheField.get(pluginManager);

                        // 注册所有已缓存的扩展点
                        int registeredCount = 0;
                        for (Map.Entry<Class<?>, List<?>> entry : extensionCache.entrySet()) {
                            for (Object extension : entry.getValue()) {
                                String className = extension.getClass().getName();

                                // 找到该扩展点所属的插件路径
                                for (String pathStr : devClassPaths) {
                                    Path pluginClassPath = Paths.get(pathStr).toAbsolutePath().normalize();

                                    // 检查类文件是否在该路径下
                                    String classFilePath = className.replace('.', '/') + ".class";
                                    Path fullPath = pluginClassPath.resolve(classFilePath);

                                    if (java.nio.file.Files.exists(fullPath)) {
                                        hotReloadManager.registerExtension(className, pluginClassPath);
                                        registeredCount++;
                                        break;
                                    }
                                }
                            }
                        }

                        log.info("已补充注册 {} 个已扫描的扩展点到插件映射", registeredCount);
                    } catch (Exception ex) {
                        log.warn("补充注册已扫描的扩展点失败（不影响后续扫描的扩展点）", ex);
                    }
                }
            } catch (Exception e) {
                log.warn("注入热重载管理器到插件管理器失败（这不影响基本热重载功能）", e);
            }

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

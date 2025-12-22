package com.cmsr.onebase.plugin.runtime.manager;

import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationEventPublisher;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginDescriptorFinder;
import org.pf4j.PropertiesPluginDescriptorFinder;

/**
 * 插件管理器
 * <p>
 * 封装PF4J的PluginManager，提供插件的加载、卸载、启动、停止等生命周期管理。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@Slf4j
public class OneBasePluginManager {

    private final PluginManager pluginManager;
    private final ApplicationEventPublisher eventPublisher;

    public OneBasePluginManager(PluginManager pluginManager, ApplicationEventPublisher eventPublisher) {
        this.pluginManager = pluginManager;
        this.eventPublisher = eventPublisher;
    }

    /**
     * 加载插件
     *
     * @param pluginPath 插件文件路径（JAR或ZIP）
     * @return 插件ID
     */
    public String loadPlugin(Path pluginPath) {
        log.info("加载插件: {}", pluginPath);
        String pluginId = null;

        // 先尝试查找是否已存在相同路径的已加载插件，减少重复加载产生的异常
        try {
            List<org.pf4j.PluginWrapper> plugins = pluginManager.getPlugins();
            if (plugins != null) {
                for (org.pf4j.PluginWrapper w : plugins) {
                    Path p = w.getPluginPath();
                    if (p != null) {
                        try {
                            if (isSamePluginPath(p, pluginPath)) {
                                pluginId = w.getPluginId();
                                break;
                            }
                        } catch (Exception ex) {
                            log.debug("比较插件路径时出错: {} -> {}", p, pluginPath);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.debug("检查已加载插件列表时发生异常（可忽略）: {}", pluginPath, ex);
        }

        // 使用单一 try 来执行加载和事件发布，处理竞态导致的 PluginAlreadyLoadedException
        try {
            if (pluginId == null) {
                pluginId = pluginManager.loadPlugin(pluginPath);
            } else {
                log.info("插件已处于加载完成状态，无需重复加载: {} -> {}", pluginPath, pluginId);
            }

            if (pluginId != null) {
                log.info("插件加载成功: {}", pluginId);
                org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginLoadedEvent(pluginId, wrapper));
            } else {
                log.error("插件加载失败: {}", pluginPath);
            }
        } catch (org.pf4j.PluginAlreadyLoadedException pae) {
            // 竞态情况下插件已被其它线程加载，尝试定位已加载的 pluginId 并发布事件
            log.info("插件已加载（竞态）: {}", pluginPath);
            try {
                for (org.pf4j.PluginWrapper w : pluginManager.getPlugins()) {
                    Path p = w.getPluginPath();
                    if (p != null && isSamePluginPath(p, pluginPath)) {
                        pluginId = w.getPluginId();
                        break;
                    }
                }
                if (pluginId != null) {
                    org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
                    eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginLoadedEvent(pluginId, wrapper));
                    log.info("已定位并发布已加载插件事件: {}", pluginId);
                } else {
                    log.warn("插件已加载但无法定位 pluginId: {}", pluginPath);
                }
            } catch (Exception ignore) {
                log.warn("在处理 PluginAlreadyLoadedException 时发生异常（已忽略）: {}", pluginPath);
            }
        } catch (Exception e) {
            log.error("加载插件或发布事件时发生错误: {}", pluginPath, e);
        }

        return pluginId;
    }

    private static Path getNormalize(Path p) {
        return p.toAbsolutePath().normalize();
    }

    /**
     * 判断已加载插件路径与待加载插件路径是否代表同一插件。
     * PF4J 对 archive 会解压为目录，所以需要兼容目录-文件的比较。
     */
    private boolean isSamePluginPath(Path loadedPath, Path archivePath) {
        Path lp = loadedPath.toAbsolutePath().normalize();
        Path ap = archivePath.toAbsolutePath().normalize();
        if (lp.equals(ap)) {
            return true;
        }
        try {
            // 如果已加载路径是目录，且目录下包含与 archive 同名的文件（例如原始 zip/jar），也认为是同一插件
            if (Files.isDirectory(lp)) {
                Path candidate = lp.resolve(ap.getFileName());
                if (Files.exists(candidate)) {
                    return true;
                }
            }
            // 如果 archivePath 在已加载目录的父目录下，也可能是同一插件（常见 plugins/xxx.zip -> plugins/xxx/ 解压）
            if (Files.isDirectory(lp) && ap.getParent() != null && lp.getParent() != null) {
                if (lp.getParent().equals(ap.getParent())) {
                    // 比较目录名与 archive 文件名（去掉扩展名）
                    String dirName = lp.getFileName().toString();
                    String fileBase = removeExtension(ap.getFileName().toString());
                    if (dirName.equals(fileBase)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignore) {
        }
        return false;
    }

    private String removeExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(0, idx) : name;
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件ID
     * @return 是否成功
     */
    public boolean unloadPlugin(String pluginId) {
        log.info("卸载插件: {}", pluginId);
        boolean ok = pluginManager.unloadPlugin(pluginId);
        if (ok) {
            try {
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginUnloadedEvent(pluginId));
            } catch (Exception ignore) {
            }
        }
        return ok;
    }

    /**
     * 启动插件
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState startPlugin(String pluginId) {
        log.info("启动插件: {}", pluginId);
        PluginState state = pluginManager.startPlugin(pluginId);
        try {
            if (state == PluginState.STARTED) {
                org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginStartedEvent(pluginId, wrapper));
            }
        } catch (Exception ignore) {
        }
        return state;
    }

    /**
     * 停止插件
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState stopPlugin(String pluginId) {
        log.info("停止插件: {}", pluginId);
        PluginState state = pluginManager.stopPlugin(pluginId);
        try {
            eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginStoppedEvent(pluginId));
        } catch (Exception ignore) {
        }
        return state;
    }

    /**
     * 删除插件
     *
     * @param pluginId 插件ID
     * @return 是否成功
     */
    public boolean deletePlugin(String pluginId) {
        log.info("删除插件: {}", pluginId);
        boolean ok = pluginManager.deletePlugin(pluginId);
        if (ok) {
            try {
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginDeletedEvent(pluginId));
            } catch (Exception ignore) {
            }
        }
        return ok;
    }

    /**
     * 获取所有插件
     *
     * @return 插件列表
     */
    public List<PluginWrapper> getPlugins() {
        return pluginManager.getPlugins();
    }

    /**
     * 获取已启动的插件
     *
     * @return 插件列表
     */
    public List<PluginWrapper> getStartedPlugins() {
        return pluginManager.getStartedPlugins();
    }

    /**
     * 获取指定插件
     *
     * @param pluginId 插件ID
     * @return 插件包装器
     */
    public Optional<PluginWrapper> getPlugin(String pluginId) {
        return Optional.ofNullable(pluginManager.getPlugin(pluginId));
    }

    /**
     * 获取插件状态
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState getPluginState(String pluginId) {
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        return wrapper != null ? wrapper.getPluginState() : null;
    }

    // ==================== 扩展点查询 ====================

    /**
     * 获取所有数据处理器
     *
     * @return 数据处理器列表
     */
    public List<DataProcessor> getDataProcessors() {
        return pluginManager.getExtensions(DataProcessor.class);
    }

    /**
     * 获取指定类型的数据处理器
     *
     * @param type 处理器类型
     * @return 数据处理器
     */
    public Optional<DataProcessor> getDataProcessor(String type) {
        return getDataProcessors().stream()
                .filter(p -> type.equals(p.type()))
                .findFirst();
    }

    /**
     * 获取所有事件监听器
     *
     * @return 事件监听器列表
     */
    public List<EventListener> getEventListeners() {
        return pluginManager.getExtensions(EventListener.class);
    }

    /**
     * 获取指定事件类型的监听器
     *
     * @param eventType 事件类型
     * @return 事件监听器列表
     */
    public List<EventListener> getEventListeners(String eventType) {
        return getEventListeners().stream()
                .filter(l -> containsEventType(l.eventTypes(), eventType) || containsEventType(l.eventTypes(), "*"))
                .sorted((a, b) -> Integer.compare(a.order(), b.order()))
                .collect(Collectors.toList());
    }

    /**
     * 检查事件类型数组是否包含指定类型
     */
    private boolean containsEventType(String[] eventTypes, String eventType) {
        if (eventTypes == null) {
            return false;
        }
        for (String type : eventTypes) {
            if (type.equals(eventType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有HTTP处理器
     * <p>
     * 注意：HttpHandler现在是纯标记接口，用于标识插件的@RestController。
     * 实际的路由处理由Spring MVC完成，无需手动路由匹配。
     * </p>
     *
     * @return HTTP处理器列表
     */
    public List<HttpHandler> getHttpHandlers() {
        return pluginManager.getExtensions(HttpHandler.class);
    }

    /**
     * 获取指定类型的所有扩展
     *
     * @param extensionClass 扩展类型
     * @param <T>            扩展类型
     * @return 扩展列表
     */
    public <T> List<T> getExtensions(Class<T> extensionClass) {
        return pluginManager.getExtensions(extensionClass);
    }

    /**
     * 获取底层的PF4J PluginManager
     *
     * @return PluginManager
     */
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * 获取已加载的插件列表（返回插件信息摘要）
     *
     * @return 插件信息列表
     */
    public List<PluginInfo> getLoadedPlugins() {
        log.debug("开始获取已加载的插件列表");
        List<org.pf4j.PluginWrapper> plugins = pluginManager.getPlugins();
        log.debug("获取到的插件列表大小: {}", plugins != null ? plugins.size() : "null");
        
        if (plugins == null) {
            log.warn("插件管理器返回了null插件列表");
            return new ArrayList<>();
        }
        
        return plugins.stream()
                .filter(Objects::nonNull)
                .map(wrapper -> {
                    log.debug("处理插件包装器: {}", wrapper.getPluginId());
                    if (wrapper.getDescriptor() == null) {
                        log.warn("插件 {} 的描述符为null", wrapper.getPluginId());
                        return new PluginInfo(
                                wrapper.getPluginId(),
                                "Unknown",
                                "Unknown",
                                wrapper.getPluginState().name()
                        );
                    }
                    return new PluginInfo(
                            wrapper.getPluginId(),
                            wrapper.getDescriptor().getPluginDescription(),
                            wrapper.getDescriptor().getVersion(),
                            wrapper.getPluginState().name()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 重新加载插件
     *
     * @param pluginId 插件ID
     * @return 新的插件状态
     */
    public PluginState reloadPlugin(String pluginId) {
        log.info("重新加载插件: {}", pluginId);
        PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new IllegalArgumentException("插件不存在: " + pluginId);
        }

        // 获取插件路径
        Path pluginPath = wrapper.getPluginPath();

        // 停止并卸载
        pluginManager.stopPlugin(pluginId);
        pluginManager.unloadPlugin(pluginId);

        // 重新加载并启动
        String newId = pluginManager.loadPlugin(pluginPath);
        PluginState state = pluginManager.startPlugin(newId);
        return state;
    }

    /**
     * 仅获取指定插件的 HttpHandler 扩展点
     *
     * @param pluginId 插件ID
     * @return HttpHandler 列表
     */
    public java.util.List<com.cmsr.onebase.plugin.api.HttpHandler> getHttpHandlers(String pluginId) {
        return pluginManager.getExtensions(com.cmsr.onebase.plugin.api.HttpHandler.class, pluginId);
    }

    /**
     * 插件信息DTO
     */
    public static class PluginInfo {
        private final String pluginId;
        private final String description;
        private final String version;
        private final String state;

        public PluginInfo(String pluginId, String description, String version, String state) {
            this.pluginId = pluginId;
            this.description = description;
            this.version = version;
            this.state = state;
        }

        public String getPluginId() {
            return pluginId;
        }

        public String getDescription() {
            return description;
        }

        public String getVersion() {
            return version;
        }

        public String getState() {
            return state;
        }
    }
}

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
     * 无参构造器（兼容通过组件扫描创建bean的场景）
     * 会创建一个最小可用的 DefaultPluginManager 和 no-op 的 ApplicationEventPublisher
     */
    // 注：已移除无参兼容构造器。Starter 采用 auto-configuration 提供 bean，宿主不应扫描 runtime 包。

    // ==================== 生命周期管理 ====================

    /**
     * 加载插件
     *
     * @param pluginPath 插件文件路径（JAR或ZIP）
     * @return 插件ID
     */
    public String loadPlugin(Path pluginPath) {
        log.info("加载插件: {}", pluginPath);
        String pluginId = pluginManager.loadPlugin(pluginPath);
        if (pluginId != null) {
            log.info("插件加载成功: {}", pluginId);
            try {
                org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginLoadedEvent(pluginId, wrapper));
            } catch (Exception ignore) {
            }
        } else {
            log.error("插件加载失败: {}", pluginPath);
        }
        return pluginId;
    }

    /**
     * 带回滚机制的插件加载
     * <p>
     * 在加载失败时自动回滚（卸载已部分加载的插件），并发布失败事件。
     * 支持完整的生命周期事件通知（加载前、加载后、加载失败）。
     * </p>
     *
     * @param pluginPath 插件文件路径（JAR或ZIP）
     * @param operator   操作者（用户ID或"system"）
     * @return 插件ID，失败时返回null
     */
    public String loadPluginWithRollback(Path pluginPath, String operator) {
        String pluginId = null;
        
        try {
            // 发布加载前事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginBeforeLoadEvent(pluginPath, operator)
            );
            
            // 执行加载
            log.info("加载插件（带回滚）: {}, 操作者: {}", pluginPath, operator);
            pluginId = pluginManager.loadPlugin(pluginPath);
            
            if (pluginId == null) {
                throw new RuntimeException("插件加载返回null，可能是插件文件格式错误或依赖冲突");
            }
            
            log.info("插件加载成功: {}", pluginId);
            
            // 发布加载后事件
            org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginLoadedEvent(pluginId, wrapper)
            );
            
            return pluginId;
            
        } catch (Exception e) {
            log.error("插件加载失败，开始回滚: {}", pluginPath, e);
            
            // 回滚操作：卸载已部分加载的插件
            if (pluginId != null) {
                try {
                    boolean unloaded = pluginManager.unloadPlugin(pluginId);
                    if (unloaded) {
                        log.info("插件回滚成功，已卸载: {}", pluginId);
                    } else {
                        log.warn("插件回滚失败，无法卸载: {}", pluginId);
                    }
                } catch (Exception rollbackEx) {
                    log.error("插件回滚过程中发生异常: {}", pluginId, rollbackEx);
                }
            }
            
            // 发布加载失败事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginLoadFailedEvent(pluginPath, pluginId, e, operator)
            );
            
            return null;
        }
    }

    /**
     * 带回滚机制的插件加载（使用默认操作者"system"）
     *
     * @param pluginPath 插件文件路径（JAR或ZIP）
     * @return 插件ID，失败时返回null
     */
    public String loadPluginWithRollback(Path pluginPath) {
        return loadPluginWithRollback(pluginPath, "system");
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
     * 带回滚机制的插件启动
     * <p>
     * 在启动失败时发布失败事件，支持完整的生命周期事件通知（启动前、启动后、启动失败）。
     * </p>
     *
     * @param pluginId 插件ID
     * @param operator 操作者（用户ID或"system"）
     * @return 插件状态
     */
    public PluginState startPluginWithRollback(String pluginId, String operator) {
        org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new IllegalArgumentException("插件不存在: " + pluginId);
        }
        
        try {
            // 发布启动前事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginBeforeStartEvent(pluginId, wrapper, operator)
            );
            
            // 执行启动
            log.info("启动插件（带回滚）: {}, 操作者: {}", pluginId, operator);
            PluginState state = pluginManager.startPlugin(pluginId);
            
            if (state == PluginState.STARTED) {
                log.info("插件启动成功: {}", pluginId);
                
                // 发布启动后事件
                eventPublisher.publishEvent(
                    new com.cmsr.onebase.plugin.runtime.event.PluginStartedEvent(pluginId, wrapper)
                );
            } else {
                throw new RuntimeException("插件启动失败，状态: " + state);
            }
            
            return state;
            
        } catch (Exception e) {
            log.error("插件启动失败: {}", pluginId, e);
            
            // 发布启动失败事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginStartFailedEvent(pluginId, wrapper, e, operator)
            );
            
            return wrapper.getPluginState();
        }
    }

    /**
     * 带回滚机制的插件启动（使用默认操作者"system"）
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState startPluginWithRollback(String pluginId) {
        return startPluginWithRollback(pluginId, "system");
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
     * 带事件通知的插件停止
     * <p>
     * 支持完整的生命周期事件通知（停止前、停止后）。
     * </p>
     *
     * @param pluginId 插件ID
     * @param operator 操作者（用户ID或"system"）
     * @return 插件状态
     */
    public PluginState stopPluginWithEvent(String pluginId, String operator) {
        org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new IllegalArgumentException("插件不存在: " + pluginId);
        }
        
        try {
            // 发布停止前事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginBeforeStopEvent(pluginId, wrapper, operator)
            );
            
            // 执行停止
            log.info("停止插件（带事件）: {}, 操作者: {}", pluginId, operator);
            PluginState state = pluginManager.stopPlugin(pluginId);
            
            log.info("插件停止完成: {}, 状态: {}", pluginId, state);
            
            // 发布停止后事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginStoppedEvent(pluginId)
            );
            
            return state;
            
        } catch (Exception e) {
            log.error("插件停止失败: {}", pluginId, e);
            throw e;
        }
    }

    /**
     * 带事件通知的插件停止（使用默认操作者"system"）
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState stopPluginWithEvent(String pluginId) {
        return stopPluginWithEvent(pluginId, "system");
    }

    /**
     * 带事件通知的插件卸载
     * <p>
     * 支持完整的生命周期事件通知（卸载前、卸载后）。
     * </p>
     *
     * @param pluginId 插件ID
     * @param operator 操作者（用户ID或"system"）
     * @return 是否成功
     */
    public boolean unloadPluginWithEvent(String pluginId, String operator) {
        org.pf4j.PluginWrapper wrapper = pluginManager.getPlugin(pluginId);
        if (wrapper == null) {
            throw new IllegalArgumentException("插件不存在: " + pluginId);
        }
        
        try {
            // 发布卸载前事件
            eventPublisher.publishEvent(
                new com.cmsr.onebase.plugin.runtime.event.PluginBeforeUnloadEvent(pluginId, wrapper, operator)
            );
            
            // 执行卸载
            log.info("卸载插件（带事件）: {}, 操作者: {}", pluginId, operator);
            boolean ok = pluginManager.unloadPlugin(pluginId);
            
            if (ok) {
                log.info("插件卸载成功: {}", pluginId);
                
                // 发布卸载后事件
                eventPublisher.publishEvent(
                    new com.cmsr.onebase.plugin.runtime.event.PluginUnloadedEvent(pluginId)
                );
            } else {
                log.warn("插件卸载失败: {}", pluginId);
            }
            
            return ok;
            
        } catch (Exception e) {
            log.error("插件卸载异常: {}", pluginId, e);
            throw e;
        }
    }

    /**
     * 带事件通知的插件卸载（使用默认操作者"system"）
     *
     * @param pluginId 插件ID
     * @return 是否成功
     */
    public boolean unloadPluginWithEvent(String pluginId) {
        return unloadPluginWithEvent(pluginId, "system");
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
     * 启动所有插件
     */
    public void startAllPlugins() {
        log.info("启动所有插件");
        pluginManager.startPlugins();
    }

    /**
     * 停止所有插件
     */
    public void stopAllPlugins() {
        log.info("停止所有插件");
        pluginManager.stopPlugins();
    }

    // ==================== 插件查询 ====================

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
        if (state == PluginState.STARTED) {
            try {
                eventPublisher.publishEvent(new com.cmsr.onebase.plugin.runtime.event.PluginReloadedEvent(newId));
            } catch (Exception ignore) {
            }
        }
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

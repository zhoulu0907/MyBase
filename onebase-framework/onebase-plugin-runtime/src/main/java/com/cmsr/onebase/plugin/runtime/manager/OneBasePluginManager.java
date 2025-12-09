package com.cmsr.onebase.plugin.runtime.manager;

import com.cmsr.onebase.plugin.api.CustomFunction;
import com.cmsr.onebase.plugin.api.DataProcessor;
import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.api.HttpHandler;
import org.pf4j.PluginManager;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 插件管理器
 * <p>
 * 封装PF4J的PluginManager，提供插件的加载、卸载、启动、停止等生命周期管理。
 * </p>
 *
 * @author matianyu
 * @date 2025-11-29
 */
@Component
public class OneBasePluginManager {

    private static final Logger log = LoggerFactory.getLogger(OneBasePluginManager.class);

    private final PluginManager pluginManager;

    public OneBasePluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

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
        } else {
            log.error("插件加载失败: {}", pluginPath);
        }
        return pluginId;
    }

    /**
     * 卸载插件
     *
     * @param pluginId 插件ID
     * @return 是否成功
     */
    public boolean unloadPlugin(String pluginId) {
        log.info("卸载插件: {}", pluginId);
        return pluginManager.unloadPlugin(pluginId);
    }

    /**
     * 启动插件
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState startPlugin(String pluginId) {
        log.info("启动插件: {}", pluginId);
        return pluginManager.startPlugin(pluginId);
    }

    /**
     * 停止插件
     *
     * @param pluginId 插件ID
     * @return 插件状态
     */
    public PluginState stopPlugin(String pluginId) {
        log.info("停止插件: {}", pluginId);
        return pluginManager.stopPlugin(pluginId);
    }

    /**
     * 删除插件
     *
     * @param pluginId 插件ID
     * @return 是否成功
     */
    public boolean deletePlugin(String pluginId) {
        log.info("删除插件: {}", pluginId);
        return pluginManager.deletePlugin(pluginId);
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
     * 获取所有自定义函数
     *
     * @return 自定义函数列表
     */
    public List<CustomFunction> getCustomFunctions() {
        return pluginManager.getExtensions(CustomFunction.class);
    }

    /**
     * 获取指定插件的自定义函数
     *
     * @param pluginId 插件ID
     * @return 自定义函数列表
     */
    public List<CustomFunction> getCustomFunctions(String pluginId) {
        return pluginManager.getExtensions(CustomFunction.class, pluginId);
    }

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
     *
     * @return HTTP处理器列表
     */
    public List<HttpHandler> getHttpHandlers() {
        return pluginManager.getExtensions(HttpHandler.class);
    }

    /**
     * 获取匹配路径的HTTP处理器
     *
     * @param path   请求路径
     * @param method 请求方法
     * @return HTTP处理器
     */
    public Optional<HttpHandler> getHttpHandler(String path, String method) {
        return getHttpHandlers().stream()
                .filter(h -> matchPath(h.pathPattern(), path) && h.method().equalsIgnoreCase(method))
                .findFirst();
    }

    /**
     * 简单路径匹配
     */
    private boolean matchPath(String pattern, String path) {
        if (pattern.equals(path)) {
            return true;
        }
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        if (pattern.contains("{")) {
            // 简单的路径变量匹配
            String regex = pattern.replaceAll("\\{[^}]+}", "[^/]+");
            return path.matches(regex);
        }
        return false;
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
        return pluginManager.getPlugins().stream()
                .map(wrapper -> new PluginInfo(
                        wrapper.getPluginId(),
                        wrapper.getDescriptor().getPluginDescription(),
                        wrapper.getDescriptor().getVersion(),
                        wrapper.getPluginState().name()
                ))
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
        pluginManager.loadPlugin(pluginPath);
        return pluginManager.startPlugin(pluginId);
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

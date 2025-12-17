package com.cmsr.onebase.plugin.runtime.listener;

import com.cmsr.onebase.plugin.runtime.event.*;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpDispatcher;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpManager;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * 插件生命周期事件监听器
 * <p>
 * 负责监听插件的生命周期事件（启动、停止、卸载、删除），
 * 并委托给PluginHttpManager和PluginHttpDispatcher处理HTTP处理器的注册/注销。
 * </p>
 * <p>
 * 设计原则：
 * - 单一职责：仅负责事件监听，不包含业务逻辑
 * - 委托模式：将HTTP处理器的注册/注销委托给专门的管理器
 * </p>
 *
 * @author matianyu
 * @date 2025-12-17
 */
@Slf4j
public class PluginLifecycleListener {

    private final OneBasePluginManager pluginManager;
    private final PluginHttpDispatcher dispatcher;
    private final PluginHttpManager httpManager;

    public PluginLifecycleListener(OneBasePluginManager pluginManager, 
                                   PluginHttpDispatcher dispatcher,
                                   PluginHttpManager httpManager) {
        this.pluginManager = pluginManager;
        this.dispatcher = dispatcher;
        this.httpManager = httpManager;
    }

    /**
     * 监听插件启动事件
     * <p>
     * 当插件启动时，获取其HttpHandler并注册到HTTP管理器和分发器。
     * </p>
     *
     * @param event 插件启动事件
     */
    @EventListener
    public void onPluginStarted(PluginStartedEvent event) {
        String pluginId = event.getPluginId();
        try {
            List<HttpHandler> handlers = pluginManager.getHttpHandlers(pluginId);
            if (handlers != null && !handlers.isEmpty()) {
                // 委托给PluginHttpManager注册
                httpManager.registerPlugin(pluginId, handlers);
                // 委托给PluginHttpDispatcher注册
                dispatcher.registerHandlers(pluginId, handlers);
                log.info("已为插件 {} 注册 {} 个HttpHandler", pluginId, handlers.size());
            } else {
                log.debug("插件 {} 没有HttpHandler可注册", pluginId);
            }
        } catch (Exception e) {
            log.error("为插件注册HttpHandler失败: {}", pluginId, e);
        }
    }

    /**
     * 监听插件停止事件
     * <p>
     * 当插件停止时，移除其HTTP路由以避免仍然可被访问。
     * </p>
     *
     * @param event 插件停止事件
     */
    @EventListener
    public void onPluginStopped(PluginStoppedEvent event) {
        String pluginId = event.getPluginId();
        try {
            // 委托给PluginHttpManager和Dispatcher注销
            httpManager.unregisterPlugin(pluginId);
            dispatcher.unregisterHandlers(pluginId);
            log.info("插件 {} 停止，已移除其路由", pluginId);
        } catch (Exception e) {
            log.error("停止时移除插件路由失败: {}", pluginId, e);
        }
    }

    /**
     * 监听插件卸载事件
     * <p>
     * 当插件卸载时，移除其HTTP路由。
     * </p>
     *
     * @param event 插件卸载事件
     */
    @EventListener
    public void onPluginUnloaded(PluginUnloadedEvent event) {
        String pluginId = event.getPluginId();
        try {
            // 委托给PluginHttpManager和Dispatcher注销
            httpManager.unregisterPlugin(pluginId);
            dispatcher.unregisterHandlers(pluginId);
            log.info("已移除插件 {} 的路由", pluginId);
        } catch (Exception e) {
            log.error("移除插件路由失败: {}", pluginId, e);
        }
    }

    /**
     * 监听插件删除事件
     * <p>
     * 当插件删除时，移除其HTTP路由。
     * </p>
     *
     * @param event 插件删除事件
     */
    @EventListener
    public void onPluginDeleted(PluginDeletedEvent event) {
        String pluginId = event.getPluginId();
        try {
            // 委托给PluginHttpManager和Dispatcher注销
            httpManager.unregisterPlugin(pluginId);
            dispatcher.unregisterHandlers(pluginId);
            log.info("插件 {} 删除，已移除其路由", pluginId);
        } catch (Exception e) {
            log.error("删除时移除插件路由失败: {}", pluginId, e);
        }
    }
}

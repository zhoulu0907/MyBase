package com.cmsr.onebase.plugin.runtime.listener;

import com.cmsr.onebase.plugin.runtime.event.*;
import com.cmsr.onebase.plugin.runtime.http.PluginHttpDispatcher;
import com.cmsr.onebase.plugin.runtime.http.HttpRoutingManager;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import com.cmsr.onebase.plugin.api.HttpHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import java.util.List;

@Slf4j
public class PluginLifecycleListener {

    private final OneBasePluginManager pluginManager;
    private final PluginHttpDispatcher dispatcher;
    private final HttpRoutingManager httpRoutingManager;

    public PluginLifecycleListener(OneBasePluginManager pluginManager, PluginHttpDispatcher dispatcher,
                                   HttpRoutingManager httpRoutingManager) {
        this.pluginManager = pluginManager;
        this.dispatcher = dispatcher;
        this.httpRoutingManager = httpRoutingManager;
    }

    @EventListener
    public void onPluginStarted(PluginStartedEvent ev) {
        String pluginId = ev.getPluginId();
        try {
            List<HttpHandler> handlers = pluginManager.getHttpHandlers(pluginId);
            if (handlers != null && !handlers.isEmpty()) {
                dispatcher.registerHandlers(pluginId, handlers);
                log.info("已为插件 {} 注册 {} 个 HttpHandler", pluginId, handlers.size());
            } else {
                log.debug("插件 {} 没有 HttpHandler 可注册", pluginId);
            }
        } catch (Exception e) {
            log.error("为插件注册 HttpHandler 失败: {}", pluginId, e);
        }
    }

    @EventListener
    public void onPluginUnloaded(PluginUnloadedEvent ev) {
        String pluginId = ev.getPluginId();
        try {
            dispatcher.unregisterHandlers(pluginId);
            try {
                httpRoutingManager.unregisterHandlers(pluginId);
            } catch (Exception ignore) {
            }
            log.info("已移除插件 {} 的路由", pluginId);
        } catch (Exception e) {
            log.error("移除插件路由失败: {}", pluginId, e);
        }
    }

    @EventListener
    public void onPluginStopped(PluginStoppedEvent ev) {
        // 停止时也清理路由以避免仍然可被访问
        String pluginId = ev.getPluginId();
        try {
            dispatcher.unregisterHandlers(pluginId);
            try {
                httpRoutingManager.unregisterHandlers(pluginId);
            } catch (Exception ignore) {
            }
            log.info("插件 {} 停止，已移除其路由", pluginId);
        } catch (Exception e) {
            log.error("停止时移除插件路由失败: {}", pluginId, e);
        }
    }

    @EventListener
    public void onPluginDeleted(PluginDeletedEvent ev) {
        String pluginId = ev.getPluginId();
        try {
            dispatcher.unregisterHandlers(pluginId);
            try {
                httpRoutingManager.unregisterHandlers(pluginId);
            } catch (Exception ignore) {
            }
            log.info("插件 {} 删除，已移除其路由", pluginId);
        } catch (Exception e) {
            log.error("删除时移除插件路由失败: {}", pluginId, e);
        }
    }
}

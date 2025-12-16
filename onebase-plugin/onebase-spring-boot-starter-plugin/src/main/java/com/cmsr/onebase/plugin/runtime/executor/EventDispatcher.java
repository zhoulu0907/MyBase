package com.cmsr.onebase.plugin.runtime.executor;

import com.cmsr.onebase.plugin.api.EventListener;
import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.PluginEvent;
import com.cmsr.onebase.plugin.runtime.context.PluginContextFactory;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 事件分发器
 * <p>
 * 负责将平台事件分发给插件的事件监听器。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-18
 */
@Slf4j
public class EventDispatcher {

    private final OneBasePluginManager pluginManager;
    private final PluginContextFactory contextFactory;

    public EventDispatcher(OneBasePluginManager pluginManager, PluginContextFactory contextFactory) {
        this.pluginManager = pluginManager;
        this.contextFactory = contextFactory;
    }

    /**
     * 分发事件
     *
     * @param event 事件
     */
    public void dispatch(PluginEvent event) {
        List<EventListener> listeners = pluginManager.getEventListeners(event.getEventType());

        if (listeners.isEmpty()) {
            log.debug("无事件监听器处理事件: {}", event.getEventType());
            return;
        }

        log.info("分发事件: {}, 监听器数量: {}", event.getEventType(), listeners.size());

        for (EventListener listener : listeners) {
            try {
                if (listener.async()) {
                    dispatchAsync(listener, event);
                } else {
                    dispatchSync(listener, event);
                }
            } catch (Exception e) {
                log.error("事件监听器执行失败: {}", listener.getClass().getName(), e);
            }
        }
    }

    /**
     * 同步分发事件
     */
    private void dispatchSync(EventListener listener, PluginEvent event) {
        String pluginId = getPluginIdForListener(listener);
        PluginContext context = contextFactory.createContext(pluginId);

        try {
            log.debug("同步执行事件监听器: {}", listener.getClass().getSimpleName());
            listener.onEvent(context, event);
        } catch (Exception e) {
            log.error("事件监听器执行失败: {}", listener.getClass().getName(), e);
        }
    }

    /**
     * 异步分发事件
     */
    @Async
    public CompletableFuture<Void> dispatchAsync(EventListener listener, PluginEvent event) {
        return CompletableFuture.runAsync(() -> {
            String pluginId = getPluginIdForListener(listener);
            PluginContext context = contextFactory.createContext(pluginId);

            try {
                log.debug("异步执行事件监听器: {}", listener.getClass().getSimpleName());
                listener.onEvent(context, event);
            } catch (Exception e) {
                log.error("异步事件监听器执行失败: {}", listener.getClass().getName(), e);
            }
        });
    }

    /**
     * 分发实体事件
     *
     * @param eventType  事件类型
     * @param entityCode 实体编码
     * @param entityId   实体ID
     * @param data       事件数据
     */
    public void dispatchEntityEvent(String eventType, String entityCode, Long entityId, java.util.Map<String, Object> data) {
        PluginEvent event = PluginEvent.entityEvent(eventType, entityCode, entityId, data);
        dispatch(event);
    }

    /**
     * 分发系统事件
     *
     * @param eventType 事件类型
     * @param data      事件数据
     */
    public void dispatchSystemEvent(String eventType, java.util.Map<String, Object> data) {
        PluginEvent event = PluginEvent.systemEvent(eventType, data);
        dispatch(event);
    }

    /**
     * 获取监听器所属的插件ID
     */
    private String getPluginIdForListener(EventListener listener) {
        // TODO: 从PF4J获取实际的插件ID
        return "unknown";
    }
}

package com.cmsr.onebase.plugin.runtime.http;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 插件HTTP管理器（简化版）
 * <p>
 * 在新的动态 Controller 注册机制下，本类主要作为 HttpHandler 的查询门面，
 * 供安全拦截器等组件查询路由归属。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Slf4j
public class PluginHttpManager {

    private final ObjectProvider<OneBasePluginManager> pluginManagerProvider;

    public PluginHttpManager(ObjectProvider<OneBasePluginManager> pluginManagerProvider) {
        this.pluginManagerProvider = pluginManagerProvider;
    }

    /**
     * 获取指定插件的处理器
     *
     * @param pluginId 插件ID
     * @return 处理器列表
     */
    public List<Object> getPluginHandlers(String pluginId) {
        OneBasePluginManager manager = pluginManagerProvider.getIfAvailable();
        if (manager == null) {
            return Collections.emptyList();
        }
        List<HttpHandler> handlers = manager.getHttpHandlers(pluginId);
        return handlers != null ? new ArrayList<>(handlers) : Collections.emptyList();
    }
}

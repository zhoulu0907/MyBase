package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.http.HttpHandlerRegistry;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.springframework.beans.factory.ObjectProvider;
import lombok.extern.slf4j.Slf4j;
// Provided via auto-configuration

import java.util.List;

/**
 * HTTP处理器初始化器
 * <p>
 * 在应用启动完成后，自动扫描并注册插件中的HTTP处理器。
 * 当插件系统禁用时（enabled=false），跳过初始化。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Slf4j
public class HttpHandlerInitializer {

    public HttpHandlerInitializer(ObjectProvider<OneBasePluginManager> oneBasePluginManagerProvider,
                                  HttpHandlerRegistry httpHandlerRegistry,
                                  PluginProperties pluginProperties) {
        // 检查插件系统是否启用
        if (!pluginProperties.isEnabled()) {
            log.info("插件系统已禁用，跳过HTTP处理器初始化");
            return;
        }

        OneBasePluginManager oneBasePluginManager = oneBasePluginManagerProvider.getIfAvailable();
        if (oneBasePluginManager == null) {
            log.warn("OneBasePluginManager bean 尚不可用，跳过 HTTP 处理器初始化（将由自动配置或后置组件处理）");
            return;
        }

        try {
            // 获取所有HTTP处理器
            List<HttpHandler> handlers = oneBasePluginManager.getHttpHandlers();

            if (handlers != null && !handlers.isEmpty()) {
                // 注册HTTP处理器
                httpHandlerRegistry.registerHandlers(handlers);
            }
        } catch (Exception e) {
            log.error("初始化HTTP处理器失败", e);
        }
    }
}

package com.cmsr.onebase.plugin.runtime.config;

import com.cmsr.onebase.plugin.api.HttpHandler;
import com.cmsr.onebase.plugin.runtime.http.HttpHandlerRegistry;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * HTTP处理器初始化器
 * <p>
 * 在应用启动完成后，自动扫描并注册插件中的HTTP处理器。
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Component
public class HttpHandlerInitializer {

    private static final Logger log = LoggerFactory.getLogger(HttpHandlerInitializer.class);

    public HttpHandlerInitializer(OneBasePluginManager oneBasePluginManager,
                                  HttpHandlerRegistry httpHandlerRegistry) {
        try {
            // 获取所有HTTP处理器
            List<HttpHandler> handlers = oneBasePluginManager.getHttpHandlers();
            
            log.info("发现 {} 个HTTP处理器", handlers.size());
            
            if (!handlers.isEmpty()) {
                // 注册HTTP处理器
                httpHandlerRegistry.registerHandlers(handlers);
                log.info("HTTP处理器注册完成");
            }
        } catch (Exception e) {
            log.error("初始化HTTP处理器失败", e);
        }
    }
}

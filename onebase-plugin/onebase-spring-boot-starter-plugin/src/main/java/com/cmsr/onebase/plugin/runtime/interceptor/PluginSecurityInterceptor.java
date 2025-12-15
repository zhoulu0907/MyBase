package com.cmsr.onebase.plugin.runtime.interceptor;

import com.cmsr.onebase.plugin.runtime.config.PluginProperties;
import com.cmsr.onebase.plugin.runtime.manager.OneBasePluginManager;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 插件安全拦截器
 * <p>
 * 统一拦截所有 /plugin/** 路由，实现以下功能：
 * <ul>
 *   <li>校验插件是否已加载且启动</li>
 *   <li>运行时兜底校验路由前缀规范</li>
 *   <li>记录插件访问日志</li>
 *   <li>预留扩展：权限校验、租户隔离、限流等</li>
 * </ul>
 * </p>
 *
 * @author chengyuansen
 * @date 2025-12-13
 */
@Component
public class PluginSecurityInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(PluginSecurityInterceptor.class);

    /**
     * 插件路由模式：/plugin/{pluginId}/...
     */
    private static final Pattern PLUGIN_ROUTE_PATTERN = Pattern.compile("^/plugin/([^/]+)/.*$");

    @Resource(name = "oneBasePluginManager")
    private OneBasePluginManager pluginManager;

    @Resource
    private PluginProperties pluginProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 仅处理 /plugin/** 路径
        if (!uri.startsWith("/plugin/")) {
            return true;
        }

        // 开发模式：跳过插件存在性校验，直接放行
        // 因为开发模式下扩展点直接从classpath加载，不会创建真实的PluginWrapper
        if (pluginProperties.isDevMode()) {
            log.debug("开发模式：跳过插件安全校验，放行请求: {}", uri);
            return true;
        }

        // 提取 pluginId
        Matcher matcher = PLUGIN_ROUTE_PATTERN.matcher(uri);
        if (!matcher.matches()) {
            log.warn("插件路由格式错误: {}", uri);
            sendError(response, 400, "插件路由格式错误，正确格式: /plugin/{pluginId}/...");
            return false;
        }

        String pluginId = matcher.group(1);

        // 1. 校验插件是否存在
        PluginWrapper plugin = pluginManager.getPlugin(pluginId).orElse(null);
        if (plugin == null) {
            log.warn("插件不存在: {}", pluginId);
            sendError(response, 404, "插件不存在: " + pluginId);
            return false;
        }

        // 2. 校验插件是否已启动
        if (plugin.getPluginState() != PluginState.STARTED) {
            log.warn("插件未启动: {}, 当前状态: {}", pluginId, plugin.getPluginState());
            sendError(response, 503, "插件未启动: " + pluginId);
            return false;
        }

        // 3. 运行时兜底校验路由前缀（双保险）
        String requiredPrefix = "/plugin/" + pluginId + "/";
        if (!uri.startsWith(requiredPrefix)) {
            log.error("插件路由前缀不符合规范: {}, 应以 {} 开头", uri, requiredPrefix);
            sendError(response, 400, "插件路由前缀不符合规范");
            return false;
        }

        // 4. 记录访问日志
        log.info("插件请求: {} {} -> 插件: {}", request.getMethod(), uri, pluginId);

        // TODO: 5. 权限校验（可选）
        // if (!hasPermission(request, pluginId)) {
        //     sendError(response, 403, "无权限访问该插件");
        //     return false;
        // }

        // TODO: 6. 租户隔离（可选）
        // setTenantContext(request);

        // TODO: 7. 限流控制（可选）
        // if (isRateLimited(pluginId, request)) {
        //     sendError(response, 429, "请求过于频繁，请稍后再试");
        //     return false;
        // }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
        // 清理上下文（如租户信息等）
        if (ex != null) {
            log.error("插件请求处理异常: {} {}", request.getMethod(), request.getRequestURI(), ex);
        }
    }

    /**
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        String json = String.format("{\"code\":%d,\"message\":\"%s\"}", status, message);
        response.getWriter().write(json);
    }
}

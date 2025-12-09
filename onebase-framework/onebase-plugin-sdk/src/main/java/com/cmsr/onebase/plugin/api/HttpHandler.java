package com.cmsr.onebase.plugin.api;

import com.cmsr.onebase.plugin.context.PluginContext;
import com.cmsr.onebase.plugin.model.HttpRequest;
import com.cmsr.onebase.plugin.model.HttpResponse;
import org.pf4j.ExtensionPoint;

/**
 * HTTP处理器扩展点
 * <p>
 * 用于提供自定义HTTP接口，扩展平台的REST API能力。
 * 插件提供的接口会被路由到：/plugin/{pluginId}/{pathPattern}
 * </p>
 *
 * <pre>
 * 使用示例：
 * {@code
 * public class OcrApiHandler implements HttpHandler {
 *     @Override
 *     public String pathPattern() { return "/ocr/recognize"; }
 *
 *     @Override
 *     public String method() { return "POST"; }
 *
 *     @Override
 *     public HttpResponse handle(PluginContext ctx, HttpRequest request) {
 *         String imageUrl = request.getBodyAsMap().get("imageUrl").toString();
 *         String result = doOcr(imageUrl);
 *         return HttpResponse.ok(Map.of("text", result));
 *     }
 * }
 * }
 * </pre>
 *
 * @author matianyu
 * @date 2025-11-29
 */
public interface HttpHandler extends ExtensionPoint {

    /**
     * 路径模式
     * <p>
     * 相对于插件根路径的子路径，如：/ocr/recognize
     * 完整访问路径为：/plugin/{pluginId}/ocr/recognize
     * </p>
     *
     * @return 路径模式
     */
    String pathPattern();

    /**
     * HTTP方法
     *
     * @return GET、POST、PUT、DELETE 等
     */
    default String method() {
        return "POST";
    }

    /**
     * 接口描述
     *
     * @return 描述信息
     */
    default String description() {
        return "";
    }

    /**
     * 是否需要认证
     *
     * @return true表示需要登录才能访问
     */
    default boolean requireAuth() {
        return true;
    }

    /**
     * 所需权限标识
     * <p>
     * 为空表示只需登录即可访问
     * </p>
     *
     * @return 权限标识数组
     */
    default String[] permissions() {
        return new String[0];
    }

    /**
     * 处理HTTP请求
     *
     * @param ctx     插件上下文
     * @param request HTTP请求对象
     * @return HTTP响应对象
     */
    HttpResponse handle(PluginContext ctx, HttpRequest request);
}

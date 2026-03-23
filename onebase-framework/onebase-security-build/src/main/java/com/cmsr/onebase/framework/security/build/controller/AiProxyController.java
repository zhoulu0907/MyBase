package com.cmsr.onebase.framework.security.build.controller;

import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.security.build.config.AiBridgeProperties;
import com.cmsr.onebase.framework.security.build.context.AiBridgeContextHolder;
import com.cmsr.onebase.framework.security.build.util.AiBridgeCryptoUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * AI 代理入口。
 *
 * <p>该控制器的目标是将 /build/ai/** 的请求透明转发到上游 AI 网关/服务（由配置项 proxyBaseUrl 决定），同时叠加
 * Onebase 的上下文信息（用户、租户、应用、KeyId）以及可选的 SM3 HMAC 签名头，便于上游做鉴权与审计。</p>
 *
 * <p>实现要点：</p>
 * <ul>
 *     <li>请求体与响应体都以流式方式转发，支持 multipart/form-data（附件上传）与大文件下载。</li>
 *     <li>对 SSE（text/event-stream）响应进行及时 flush，保证事件实时到达客户端。</li>
 *     <li>过滤 hop-by-hop headers，避免代理层协议冲突（如 connection/transfer-encoding/content-length）。</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/build/ai")
public class AiProxyController {

    private final AiBridgeProperties properties;

    /**
     * 代理 /build/ai/{*path} 的所有请求到上游。
     *
     * <p>注意：这里使用 HttpServletRequest/Response 做“协议层”转发，不依赖 Spring 的 @RequestBody 绑定，
     * 以避免 multipart/SSE 等场景下框架对 body 的提前消费或缓存。</p>
     */
    @RequestMapping("/{*path}")
    @ApiSignIgnore
    public void proxy(HttpServletRequest request,
                      HttpServletResponse response,
                      @PathVariable("path") String path) throws Exception {
        String base = StringUtils.trimToEmpty(properties.getProxyBaseUrl());
        if (StringUtils.isBlank(base)) {
            // 未配置上游时，返回 502，让调用方明确是网关/上游不可用
            response.setStatus(HttpStatus.BAD_GATEWAY.value());
            return;
        }
        String query = request.getQueryString();
        String fullPath = path + (StringUtils.isNotBlank(query) ? "?" + query : "");
        boolean hasSlash = base.endsWith("/") || fullPath.startsWith("/");
        String url = hasSlash ? (base + fullPath) : (base + "/" + fullPath);

        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url)).version(HttpClient.Version.HTTP_1_1);
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        boolean hasBody = hasBody(request, method);
        // 附件上传与大 body 场景必须走流式转发，避免整体读入内存
        builder.method(method, hasBody ? bodyPublisher(request) : HttpRequest.BodyPublishers.noBody());

        Collections.list(request.getHeaderNames()).forEach(name -> {
            if (!isHopByHopHeader(name)) {
                // 同名多值 header 需要逐个转发（例如 Cookie、Accept、部分 multipart 相关 header）
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    builder.header(name, values.nextElement());
                }
            }
        });

        // 从上下文注入业务侧 header（供上游鉴权/审计/隔离使用）
        AiBridgeContextHolder.Context ctx = AiBridgeContextHolder.get();
        String reqId = Objects.toString(ctx != null ? ctx.getRequestId() : null, UUID.randomUUID().toString());
        String ts = String.valueOf(System.currentTimeMillis());
        String nonce = UUID.randomUUID().toString().replace("-", "");
        if (ctx != null && StringUtils.isNotBlank(ctx.getKeyId())) {
            builder.header("X-AI-KeyId", ctx.getKeyId());
        }
        builder.header("X-AI-Request-Id", reqId);
        String userId = ctx != null ? StringUtils.defaultString(ctx.getUserId()) : "";
        String tenantId = ctx != null ? StringUtils.defaultString(ctx.getTenantId()) : "";
        String appId = ctx != null ? StringUtils.defaultString(ctx.getAppId()) : "";
        if (StringUtils.isNotBlank(userId)) builder.header("X-AI-User-Id", userId);
        if (StringUtils.isNotBlank(tenantId)) builder.header("X-AI-Tenant-Id", tenantId);
        if (StringUtils.isNotBlank(appId)) builder.header("X-AI-App-Id", appId);
        if (StringUtils.isNotBlank(properties.getSm3Key())) {
            // canonical 严格使用 /build/ai 之后的 path，确保上游可用同一规则验签
            String canonical = method + "|" + request.getRequestURI().replaceFirst("^/build/ai", "") + "|" + ts + "|" + nonce + "|" + userId + "|" + tenantId + "|" + appId + "|";
            String sig = AiBridgeCryptoUtils.hmacSm3Hex(properties.getSm3Key(), canonical);
            builder.header("X-AI-Timestamp", ts);
            builder.header("X-AI-Nonce", nonce);
            builder.header("X-AI-Signature", sig);
        }

        HttpResponse<InputStream> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
        response.setStatus(resp.statusCode());
        resp.headers().map().forEach((k, v) -> {
            if (!isHopByHopHeader(k)) {
                // 原样回写上游响应头，便于客户端获取如 content-type、cache-control 等信息
                for (String hv : v) {
                    response.addHeader(k, hv);
                }
            }
        });

        if ("HEAD".equals(method) || resp.statusCode() == 204 || resp.statusCode() == 304) {
            // 这些响应按语义不应包含 body
            response.flushBuffer();
            return;
        }

        boolean sse = resp.headers()
                .firstValue("content-type")
                .map(ct -> ct.toLowerCase(Locale.ROOT).contains("text/event-stream"))
                .orElse(false);

        try (InputStream in = resp.body(); OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) >= 0) {
                out.write(buf, 0, n);
                if (sse) {
                    // SSE 需要边写边 flush，否则客户端可能长时间收不到事件
                    response.flushBuffer();
                }
            }
            out.flush();
        }
    }

    /**
     * 将 Servlet request 的输入流桥接到 JDK HttpClient 的 BodyPublisher。
     *
     * <p>使用 ofInputStream 能在保持 multipart 边界与原始 body 不变的情况下进行转发。</p>
     */
    private static HttpRequest.BodyPublisher bodyPublisher(HttpServletRequest request) {
        return HttpRequest.BodyPublishers.ofInputStream(() -> {
            try {
                return request.getInputStream();
            } catch (Exception e) {
                throw new UncheckedIOException(e instanceof java.io.IOException io ? io : new java.io.IOException(e));
            }
        });
    }

    /**
     * 判断该 HTTP 方法是否应该携带 body。
     *
     * <p>GET/HEAD/OPTIONS 通常不带 body；其余方法仅在存在 content-length 或 transfer-encoding 时视为有 body。</p>
     */
    private static boolean hasBody(HttpServletRequest request, String method) {
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method)) {
            return false;
        }
        long contentLength = request.getContentLengthLong();
        if (contentLength > 0) {
            return true;
        }
        String transferEncoding = request.getHeader("transfer-encoding");
        return StringUtils.isNotBlank(transferEncoding);
    }

    /**
     * hop-by-hop headers（逐跳头）不应被代理转发。
     *
     * <p>参考 RFC 7230：这些头的语义仅对单一连接生效，透传可能导致上游/下游协议栈行为异常。</p>
     */
    private static boolean isHopByHopHeader(String headerName) {
        if (StringUtils.isBlank(headerName)) {
            return true;
        }
        return equalsAnyIgnoreCase(headerName,
                "host",
                "connection",
                "keep-alive",
                "proxy-authenticate",
                "proxy-authorization",
                "te",
                "trailers",
                "transfer-encoding",
                "upgrade",
                "content-length");
    }

    private static boolean equalsAnyIgnoreCase(String s, String... arr) {
        for (String a : arr) {
            if (s.equalsIgnoreCase(a)) return true;
        }
        return false;
    }
}

package com.cmsr.onebase.framework.security.build.controller;

import com.cmsr.onebase.framework.security.build.config.AiBridgeProperties;
import com.cmsr.onebase.framework.security.build.context.AiBridgeContextHolder;
import com.cmsr.onebase.framework.security.build.util.AiBridgeCryptoUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/build/ai")
public class AiProxyController {

    private final AiBridgeProperties properties;

    @RequestMapping("/{*path}")
    public ResponseEntity<byte[]> proxy(HttpServletRequest request,
                                        @PathVariable("path") String path,
                                        @RequestBody(required = false) byte[] body) throws Exception {
        String base = StringUtils.trimToEmpty(properties.getProxyBaseUrl());
        if (StringUtils.isBlank(base)) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
        String query = request.getQueryString();
        String fullPath = path + (StringUtils.isNotBlank(query) ? "?" + query : "");
        String url = base.endsWith("/") ? (base + fullPath) : (base + "/" + fullPath);

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        switch (method) {
            case "GET" -> builder.GET();
            case "DELETE" -> builder.DELETE();
            case "POST" -> builder.POST(bodyPublisher(body));
            case "PUT" -> builder.PUT(bodyPublisher(body));
            case "PATCH" -> builder.method("PATCH", bodyPublisher(body));
            default -> builder.method(method, bodyPublisher(body));
        }
        Collections.list(request.getHeaderNames()).forEach(h -> {
            if (!equalsAnyIgnoreCase(h, "host", "content-length")) {
                builder.header(h, request.getHeader(h));
            }
        });
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
            String canonical = method + "|" + request.getRequestURI().replaceFirst("^/build/ai", "") + "|" + ts + "|" + nonce + "|" + userId + "|" + tenantId + "|" + appId + "|";
            String sig = AiBridgeCryptoUtils.hmacSm3Hex(properties.getSm3Key(), canonical);
            builder.header("X-AI-Timestamp", ts);
            builder.header("X-AI-Nonce", nonce);
            builder.header("X-AI-Signature", sig);
        }

        HttpResponse<byte[]> resp = client.send(builder.build(), HttpResponse.BodyHandlers.ofByteArray());
        MultiValueMap<String, String> headers = new org.springframework.util.LinkedMultiValueMap<>();
        resp.headers().map().forEach((k, v) -> headers.put(k, v));
        return new ResponseEntity<>(resp.body(), headers, HttpStatus.valueOf(resp.statusCode()));
    }

    private static HttpRequest.BodyPublisher bodyPublisher(byte[] body) {
        return body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofByteArray(body);
    }

    private static boolean equalsAnyIgnoreCase(String s, String... arr) {
        for (String a : arr) {
            if (s.equalsIgnoreCase(a)) return true;
        }
        return false;
    }
}

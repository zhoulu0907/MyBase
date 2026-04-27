package com.cmsr.controller;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.framework.ratelimiter.core.annotation.RateLimiter;
import com.cmsr.onebase.framework.ratelimiter.core.keyresolver.impl.ClientIpRateLimiterKeyResolver;
import com.cmsr.onebase.module.ai.api.dto.ChatProxyRequest;
import com.cmsr.onebase.module.ai.api.dto.ChatProxyResponse;
import com.cmsr.onebase.module.ai.api.dto.LlmProviderVO;
import com.cmsr.onebase.module.ai.api.enums.LlmProvider;
import com.cmsr.service.ChatProxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin-api/ai")
@Slf4j
public class ChatProxyController {

    @Resource
    private ChatProxyService chatProxyService;

    @GetMapping("/chat/providers")
    public List<LlmProviderVO> getProviders() {
        List<LlmProviderVO> providers = new ArrayList<>();
        for (LlmProvider provider : LlmProvider.values()) {
            LlmProviderVO vo = new LlmProviderVO();
            vo.setName(provider.getName());
            vo.setBaseUrl(provider.getBaseUrl());
            vo.setDefaultModel(provider.getDefaultModel());
            providers.add(vo);
        }
        return providers;
    }

    @PostMapping("/chat")
    @RateLimiter(time = 10, count = 60, keyResolver = ClientIpRateLimiterKeyResolver.class,
            message = "请求过于频繁，请稍后再试")
    public Object chat(@RequestBody ChatProxyRequest request) {
        Long tenantId = TenantContextHolder.getTenantId();
        log.info("[ChatProxy] tenantId={}, provider={}, model={}", tenantId, request.getProvider(), request.getModel());

        if (!StringUtils.hasText(request.getProvider())) {
            return Collections.singletonMap("code", 400);
        }
        if (!StringUtils.hasText(request.getApiKey())) {
            return Collections.singletonMap("code", 400);
        }
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            return Collections.singletonMap("code", 400);
        }

        return chatProxyService.chat(request);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @RateLimiter(time = 10, count = 30, keyResolver = ClientIpRateLimiterKeyResolver.class,
            message = "请求过于频繁，请稍后再试")
    public Flux<String> streamChat(@RequestBody ChatProxyRequest request) {
        Long tenantId = TenantContextHolder.getTenantId();
        log.info("[ChatProxy] stream request, tenantId={}", tenantId);

        Flux<ChatProxyResponse> flux = chatProxyService.streamChat(request);

        return flux.map(chunk -> {
            if (Boolean.TRUE.equals(chunk.getDone())) {
                return "data: {\"done\": true}\n\n";
            } else {
                String json = "{\"delta\":\"" + escapeJson(chunk.getDelta()) + "\"}";
                return "data: " + json + "\n\n";
            }
        });
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
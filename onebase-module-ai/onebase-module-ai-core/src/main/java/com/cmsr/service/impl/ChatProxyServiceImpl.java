package com.cmsr.service.impl;

import com.cmsr.onebase.module.ai.api.dto.ChatProxyRequest;
import com.cmsr.onebase.module.ai.api.dto.ChatProxyResponse;
import com.cmsr.onebase.module.ai.api.dto.MessageDTO;
import com.cmsr.onebase.module.ai.api.enums.LlmProvider;
import com.cmsr.onebase.module.system.framework.security.core.PwdEnHelper;
import com.cmsr.service.ChatProxyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatProxyServiceImpl implements ChatProxyService {

    @Resource
    private PwdEnHelper pwdEnHelper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public Object chat(ChatProxyRequest request) {
        if (Boolean.TRUE.equals(request.getStream())) {
            return streamChat(request);
        }
        return nonStreamChat(request);
    }

    @Override
    public Flux<ChatProxyResponse> streamChat(ChatProxyRequest request) {
        String apiKey = decryptApiKey(request.getApiKey());
        String baseUrl = resolveBaseUrl(request.getProvider(), request.getBaseUrl());
        String model = request.getModel();
        List<MessageDTO> messages = request.getMessages();
        String systemPrompt = request.getSystemPrompt();

        String requestBody = buildOpenAIRequestBody(model, messages, systemPrompt, true);
        String url = baseUrl + "/v1/chat/completions";

        log.info("[ChatProxy] stream request, provider={}, baseUrl={}, model={}", request.getProvider(), baseUrl, model);

        return Flux.create(sink -> {
            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(120))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<java.io.InputStream> response = httpClient.send(httpRequest,
                        HttpResponse.BodyHandlers.ofInputStream());

                if (response.statusCode() != 200) {
                    String errorBody = new String(response.body().readAllBytes());
                    log.error("AI API error: status={}, body={}", response.statusCode(), errorBody);
                    sink.error(new RuntimeException("AI API error: " + response.statusCode() + " - " + errorBody));
                    return;
                }

                try (java.io.InputStream in = response.body()) {
                    byte[] buf = new byte[8192];
                    StringBuilder buffer = new StringBuilder();
                    int n;

                    while ((n = in.read(buf)) >= 0) {
                        buffer.append(new String(buf, 0, n));
                        String content = buffer.toString();

                        int lineStart = 0;
                        while ((lineStart = content.indexOf("data: ")) != -1) {
                            int lineEnd = content.indexOf("\n", lineStart);
                            if (lineEnd == -1) {
                                break;
                            }
                            String line = content.substring(lineStart + 6, lineEnd).trim();
                            content = content.substring(lineEnd + 1);

                            if (line.equals("[DONE]")) {
                                sink.next(ChatProxyResponse.done());
                                sink.complete();
                                return;
                            }

                            try {
                                Map<String, Object> chunk = objectMapper.readValue(line, Map.class);
                                List<Map<String, Object>> choices = (List<Map<String, Object>>) chunk.get("choices");
                                if (choices != null && !choices.isEmpty()) {
                                    Map<String, Object> delta = (Map<String, Object>) choices.get(0).get("delta");
                                    if (delta != null) {
                                        String contentDelta = (String) delta.get("content");
                                        if (contentDelta != null && !contentDelta.isEmpty()) {
                                            sink.next(ChatProxyResponse.delta(contentDelta));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                log.debug("Failed to parse SSE chunk: {}", line);
                            }
                        }
                        buffer = new StringBuilder(content);
                    }
                    sink.next(ChatProxyResponse.done());
                    sink.complete();
                }
            } catch (Exception e) {
                log.error("Stream chat error", e);
                sink.error(e);
            }
        });
    }

    private Object nonStreamChat(ChatProxyRequest request) {
        String apiKey = decryptApiKey(request.getApiKey());
        String baseUrl = resolveBaseUrl(request.getProvider(), request.getBaseUrl());
        String model = request.getModel();
        List<MessageDTO> messages = request.getMessages();
        String systemPrompt = request.getSystemPrompt();

        String requestBody = buildOpenAIRequestBody(model, messages, systemPrompt, false);
        String url = baseUrl + "/v1/chat/completions";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(120))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("AI API error: status={}, body={}", response.statusCode(), response.body());
                return Map.of("error", "AI API error: " + response.statusCode());
            }

            Map<String, Object> result = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) result.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    return Map.of("content", message.get("content"));
                }
            }
            return Map.of("content", "");
        } catch (Exception e) {
            log.error("Non-stream chat error", e);
            return Map.of("error", e.getMessage());
        }
    }

    private String buildOpenAIRequestBody(String model, List<MessageDTO> messages,
                                          String systemPrompt, boolean stream) {
        try {
            List<Map<String, String>> formattedMessages = messages.stream()
                    .map(m -> {
                        Map<String, String> msg = new HashMap<>();
                        msg.put("role", m.getRole());
                        msg.put("content", m.getContent());
                        return msg;
                    })
                    .collect(Collectors.toList());

            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                Map<String, String> systemMsg = new HashMap<>();
                systemMsg.put("role", "system");
                systemMsg.put("content", systemPrompt);
                formattedMessages.add(0, systemMsg);
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", formattedMessages);
            requestBody.put("stream", stream);

            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            log.error("Failed to build request body", e);
            throw new RuntimeException("Failed to build request body", e);
        }
    }

    private String resolveBaseUrl(String provider, String requestBaseUrl) {
        if (requestBaseUrl != null && !requestBaseUrl.trim().isEmpty()) {
            String url = requestBaseUrl.trim();
            if (url.endsWith("/")) {
                url = url.substring(0, url.length() - 1);
            }
            return url;
        }
        LlmProvider llmProvider = LlmProvider.fromName(provider);
        if (llmProvider != null) {
            return llmProvider.getBaseUrl();
        }
        throw new IllegalArgumentException("Unsupported provider: " + provider + " and no baseUrl provided");
    }

    private String decryptApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("apiKey cannot be empty");
        }
        try {
            return pwdEnHelper.decryptHexStr(apiKey);
        } catch (Exception e) {
            log.warn("SM2 decryption failed or disabled, using original apiKey");
            return apiKey;
        }
    }
}
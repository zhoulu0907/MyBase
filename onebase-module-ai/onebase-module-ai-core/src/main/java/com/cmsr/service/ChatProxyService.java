package com.cmsr.service;

import com.cmsr.onebase.module.ai.api.dto.ChatProxyRequest;
import com.cmsr.onebase.module.ai.api.dto.ChatProxyResponse;
import reactor.core.publisher.Flux;

public interface ChatProxyService {
    Object chat(ChatProxyRequest request);
    Flux<ChatProxyResponse> streamChat(ChatProxyRequest request);
}
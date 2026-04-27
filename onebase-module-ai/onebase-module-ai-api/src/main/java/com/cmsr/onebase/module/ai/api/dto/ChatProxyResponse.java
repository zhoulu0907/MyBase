package com.cmsr.onebase.module.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatProxyResponse {
    private String delta;
    private Boolean done;

    public static ChatProxyResponse delta(String content) {
        return new ChatProxyResponse(content, false);
    }

    public static ChatProxyResponse done() {
        return new ChatProxyResponse(null, true);
    }
}
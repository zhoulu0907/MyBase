package com.cmsr.onebase.module.ai.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LlmProvider {

    OPENAI("OpenAI", "https://api.openai.com", "gpt-4o"),
    DEEPSEEK("DeepSeek", "https://api.deepseek.com", "deepseek-chat"),
    ANTHROPIC("Anthropic", "https://api.anthropic.com", "claude-sonnet-4-20250514"),
    CUSTOM("Custom", "", "");

    private final String name;
    private final String baseUrl;
    private final String defaultModel;

    public static LlmProvider fromName(String name) {
        if (name == null) {
            return null;
        }
        for (LlmProvider provider : values()) {
            if (provider.name.equalsIgnoreCase(name)) {
                return provider;
            }
        }
        return null;
    }
}
package com.cmsr.onebase.module.ai.api.dto;

import lombok.Data;

@Data
public class LlmProviderVO {
    private String name;
    private String baseUrl;
    private String defaultModel;
}

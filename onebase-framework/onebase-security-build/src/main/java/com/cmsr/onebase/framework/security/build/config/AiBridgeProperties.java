package com.cmsr.onebase.framework.security.build.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

@ConfigurationProperties(prefix = "onebase.ai.bridge")
@RefreshScope
@Validated
@Data
public class AiBridgeProperties {

    private boolean enabled = true;

    private String sm3Key = "";

    private String proxyBaseUrl = "";

    private List<String> pathPatterns = Collections.emptyList();
}

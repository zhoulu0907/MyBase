package com.cmsr.onebase.module.flow.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：huangjie
 * @Date：2025/12/6 23:43
 */
@Configuration
public class FlowProperties {

    @Value("${lite-flow.version-tag:runtime}")
    private String versionTag;

    public Long getVersionTag() {
        return versionTag.equals("build") ? 0L : 1L;
    }

}

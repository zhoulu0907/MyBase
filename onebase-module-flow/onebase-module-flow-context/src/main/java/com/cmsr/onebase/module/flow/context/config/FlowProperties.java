package com.cmsr.onebase.module.flow.context.config;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：huangjie
 * @Date：2025/12/6 23:43
 */
@Configuration
public class FlowProperties {

    @Value("${lite-flow.version-tag:-1}")
    private Long versionTag;

    public Long getVersionTag() {
        if (versionTag == -1) {
            return ApplicationManager.getRequiredVersionTag();
        }
        return versionTag;
    }
}

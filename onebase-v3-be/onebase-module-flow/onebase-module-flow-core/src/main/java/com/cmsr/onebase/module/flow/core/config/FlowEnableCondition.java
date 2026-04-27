package com.cmsr.onebase.module.flow.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Author：huangjie
 * @Date：2025/9/18 14:07
 */
@Slf4j
public class FlowEnableCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        if (environment.getProperty("liteflow.enable", Boolean.class, false)) {
            log.debug("liteflow.enable=true, 启用流程运行环境");
            return true;
        } else {
            return false;
        }
    }

}

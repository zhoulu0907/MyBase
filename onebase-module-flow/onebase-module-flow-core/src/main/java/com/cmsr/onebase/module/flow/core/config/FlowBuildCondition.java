package com.cmsr.onebase.module.flow.core.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Author：huangjie
 * @Date：2025/9/18 14:07
 */
public class FlowBuildCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        if (!environment.getProperty("liteflow.enable", Boolean.class, false)) {
            return true;
        } else {
            return false;
        }
    }

}

package com.cmsr.onebase.module.flow.context.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：huangjie
 * @Date：2025/12/6 23:43
 */
@Data
@Configuration
public class FlowProperties {

    @Value("${lite-flow.version-tag:1}")
    private Long versionTag;

}

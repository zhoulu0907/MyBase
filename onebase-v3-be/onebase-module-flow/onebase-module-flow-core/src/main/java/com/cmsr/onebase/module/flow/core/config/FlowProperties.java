package com.cmsr.onebase.module.flow.core.config;

import com.cmsr.onebase.framework.common.enums.VersionTagEnum;
import org.apache.commons.lang3.Strings;
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

    /**
     * 可配置的跟踪流程ID，用于调试特定流程
     * 设置为需要跟踪的流程ID后，该流程的所有详细日志都会输出
     * 设置为 null 或不配置则不进行特定流程跟踪
     */
    @Value("${flow.trace.process-id:195502410512138240}")
    private Long traceProcessId;

    /**
     * 获取版本标识，默认加载为运行态，所以这样判断build值，配置其他任何乱七八糟的值或者空值，都会默认为运行态
     *
     * @return
     */
    public Long getVersionTag() {
        return Strings.CS.equals(versionTag, "build") ? VersionTagEnum.BUILD.getValue() : VersionTagEnum.RUNTIME.getValue();
    }

    public Long getTraceProcessId() {
        return traceProcessId;
    }

}

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
     * 获取版本标识，默认加载为运行态，所以这样判断build值，配置其他任何乱七八糟的值或者空值，都会默认为运行态
     *
     * @return
     */
    public Long getVersionTag() {
        return Strings.CS.equals(versionTag, "build") ? VersionTagEnum.BUILD.getValue() : VersionTagEnum.RUNTIME.getValue();
    }

}

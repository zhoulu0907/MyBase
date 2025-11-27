package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

public enum CollectStatus {
    NONE("none", "未采集"),
    SUCCESS("success", "采集成功"),
    FAILED("failed", "采集失败"),
    RUNNING("running", "采集中"),
    REQUIRED("required", "需重新采集");

    @Getter
    @EnumValue
    private final String value;

    @Getter
    private final String description;

    CollectStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static CollectStatus parse(String collectStatus) {
        for (CollectStatus status : CollectStatus.values()) {
            if (StringUtils.equals(status.getValue(), collectStatus)) {
                return status;
            }
        }
        throw ServiceExceptionUtil.exception(EtlErrorCodeConstants.INVALID_COLLECT_STATUS);
    }
}

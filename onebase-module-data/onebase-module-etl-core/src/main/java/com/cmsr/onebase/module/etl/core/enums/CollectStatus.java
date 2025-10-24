package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import lombok.Getter;

public enum CollectStatus {
    NONE(0, "未采集"),
    SUCCESS(1, "采集成功"),
    FAILED(2, "采集失败"),
    RUNNING(3, "采集中");

    @Getter
    private Integer value;

    @Getter
    private String description;

    CollectStatus(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    public static CollectStatus parse(Integer collectStatus) {
        for (CollectStatus status : CollectStatus.values()) {
            if (status.getValue().equals(collectStatus)) {
                return status;
            }
        }
        throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.INVALID_COLLECT_STATUS);
    }
}

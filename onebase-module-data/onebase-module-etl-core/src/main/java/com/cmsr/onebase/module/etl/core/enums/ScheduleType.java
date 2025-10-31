package com.cmsr.onebase.module.etl.core.enums;

import com.cmsr.onebase.framework.common.exception.util.ServiceExceptionUtil;
import org.apache.commons.lang3.StringUtils;

public enum ScheduleType {
    FIXED("fixed"),
    OBSERVE("observe"),
    MANUALLY("manually");

    private final String value;

    ScheduleType(String value) {
        this.value = value;
    }

    public static ScheduleType of(String scheduleType) {
        if (StringUtils.isBlank(scheduleType)) {
            throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
        }

        for (ScheduleType type : ScheduleType.values()) {
            if (StringUtils.equals(type.value, scheduleType)) {
                return type;
            }
        }
        throw ServiceExceptionUtil.exception(ETLErrorCodeConstants.ILLEGAL_SCHEDULE_TYPE);
    }

    public enum Fixed {
        ;
    }
}

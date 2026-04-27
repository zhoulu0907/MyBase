package com.cmsr.onebase.module.etl.core.enums;

public enum ScheduleJobStatus {
    INITIALIZED,
    SUCCESS,
    FAILED,
    RUNNING,
    WAITING_FOR_RESOURCE,
    UNKNOWN;

    public String getValue() {
        return this.name().toLowerCase();
    }
}

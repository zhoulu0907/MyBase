package com.cmsr.onebase.module.etl.core.enums;

public enum ScheduleType {
    FIXED,
    OBSERVE,
    MANUALLY;

    public enum Fixed {
        ONCE,
        DAILY,
        WEEKLY,
        MONTHLY,
        ANNUALY,
        CUSTOM;
    }

    public enum Observe {
        ADD, MODIFY, DELETE;
    }
}

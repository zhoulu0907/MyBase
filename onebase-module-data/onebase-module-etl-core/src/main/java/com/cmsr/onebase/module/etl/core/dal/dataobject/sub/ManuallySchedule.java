package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

public class ManuallySchedule implements ScheduleConfig {
    @Override
    public boolean isScheduled() {
        return false;
    }
}

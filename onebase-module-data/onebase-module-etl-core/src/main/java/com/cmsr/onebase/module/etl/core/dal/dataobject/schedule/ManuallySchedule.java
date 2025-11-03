package com.cmsr.onebase.module.etl.core.dal.dataobject.schedule;

public class ManuallySchedule implements ScheduleConfig {
    @Override
    public boolean isScheduled() {
        return false;
    }
}

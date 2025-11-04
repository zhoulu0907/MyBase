package com.cmsr.onebase.module.etl.core.dal.dataobject.schedule;

import lombok.Data;

import java.util.Set;

@Data
public class ObserveSchedule implements ScheduleConfig {
    private Set<String> triggers;

    @Override
    public boolean isScheduled() {
        return true;
    }
}

package com.cmsr.onebase.module.etl.core.dal.dataobject.sub;

import lombok.Data;

import java.util.List;

@Data
public class ObserveSchedule implements ScheduleConfig {
    private List<String> updateTrigger;

    @Override
    public boolean isScheduled() {
        return true;
    }
}

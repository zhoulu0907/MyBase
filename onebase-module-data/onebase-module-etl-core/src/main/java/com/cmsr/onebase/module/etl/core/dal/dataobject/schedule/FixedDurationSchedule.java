package com.cmsr.onebase.module.etl.core.dal.dataobject.schedule;

import com.cmsr.onebase.module.etl.core.enums.ScheduleType;

public class FixedDurationSchedule implements ScheduleConfig {

    private ScheduleType.Fixed durationType;

    @Override
    public boolean isScheduled() {
        return true;
    }
}

package com.cmsr.onebase.module.etl.core.dal.dataobject.schedule;

import lombok.Data;

import java.util.List;

@Data
public class FixedDurationSchedule implements ScheduleConfig {

    private String repeatType;

    private List<String> repeatWeek;

    private List<String> repeatDay;

    private String triggerDate;

    private String triggerTime;


    @Override
    public boolean isScheduled() {
        return true;
    }
}

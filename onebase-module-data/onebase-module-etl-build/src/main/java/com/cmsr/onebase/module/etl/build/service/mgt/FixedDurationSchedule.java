package com.cmsr.onebase.module.etl.build.service.mgt;

import lombok.Data;

import java.util.List;

@Data
public class FixedDurationSchedule {

    private String repeatType;

    private List<String> repeatWeek;

    private List<String> repeatDay;

    private String triggerDate;

    private String triggerTime;

}

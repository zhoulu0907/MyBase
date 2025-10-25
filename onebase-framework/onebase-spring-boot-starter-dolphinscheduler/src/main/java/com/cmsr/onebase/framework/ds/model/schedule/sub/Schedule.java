package com.cmsr.onebase.framework.ds.model.schedule.sub;

import lombok.Data;

@Data
public class Schedule {

    private String startTime;

    private String endTime;

    private String cronTab;

    // example value: Asia/Shanghai
    private String timezoneId = "Asia/Shanghai";
}

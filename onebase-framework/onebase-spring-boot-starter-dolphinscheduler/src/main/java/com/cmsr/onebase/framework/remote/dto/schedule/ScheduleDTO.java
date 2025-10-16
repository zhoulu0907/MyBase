package com.cmsr.onebase.framework.remote.dto.schedule;

import lombok.Data;

/**
 * 调度窗口 DTO
 */
@Data
public class ScheduleDTO {
    private String startTime;
    private String endTime;
    private String crontab;
    private String timezoneId = "Asia/Shanghai";
}


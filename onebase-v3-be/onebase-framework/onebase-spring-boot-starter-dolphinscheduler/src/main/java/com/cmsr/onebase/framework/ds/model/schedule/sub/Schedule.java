package com.cmsr.onebase.framework.ds.model.schedule.sub;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Schedule {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime = LocalDateTime.now().withYear(2999);

    private String crontab;

    // example value: Asia/Shanghai
    private String timezoneId = "Asia/Shanghai";

    @Override
    public String toString() {
        return JsonUtils.toJsonString(this);
    }
}

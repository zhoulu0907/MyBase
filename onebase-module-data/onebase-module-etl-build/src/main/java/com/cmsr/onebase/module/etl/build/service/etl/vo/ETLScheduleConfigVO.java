package com.cmsr.onebase.module.etl.build.service.etl.vo;

import com.cmsr.onebase.module.etl.core.dal.dataobject.sub.ScheduleConfig;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import lombok.Data;

@Data
public class ETLScheduleConfigVO {

    private String etlName;

    private ScheduleType scheduleStrategy;

    private ScheduleConfig config;
}

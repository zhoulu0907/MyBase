package com.cmsr.onebase.module.etl.build.service.etl.vo;

import com.cmsr.onebase.module.etl.core.dal.dataobject.schedule.ScheduleConfig;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleConfigVO {

    @NotNull
    private Long applicationId;

    @NotNull
    private Long workflowId;

    @NotBlank
    private String scheduleStrategy;

    @NotNull
    private ScheduleConfig config;

    @NotNull
    private Integer enableStatus;

    public ScheduleType getScheduleStrategy() {
        return ScheduleType.of(this.scheduleStrategy);
    }
}

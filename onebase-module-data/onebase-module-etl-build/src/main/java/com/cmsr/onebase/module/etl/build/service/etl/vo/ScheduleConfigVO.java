package com.cmsr.onebase.module.etl.build.service.etl.vo;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
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
    private String flowName;

    @NotBlank
    private String scheduleStrategy;

    private String config;

    @NotNull
    private Integer enableStatus;

    public void setConfig(Object scheduleConfig) {
        if (scheduleConfig == null) {
            return;
        }
        this.config = JsonUtils.toJsonString(scheduleConfig);
    }

    public ScheduleType getScheduleStrategy() {
        return ScheduleType.of(this.scheduleStrategy);
    }
}

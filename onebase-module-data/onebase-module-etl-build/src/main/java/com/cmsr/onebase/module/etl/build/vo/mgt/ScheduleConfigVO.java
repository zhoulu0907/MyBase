package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.cmsr.onebase.module.etl.core.enums.ScheduleType;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "数据工厂 - Etl - 调度配置请求VO")
@Data
public class ScheduleConfigVO {

    @Schema(description = "应用ID")
    @NotNull
    private Long applicationId;

    @Schema(description = "ETL ID")
    @NotNull
    private Long workflowId;

    @Schema(description = "ETL名称")
    @NotBlank
    private String flowName;

    @Schema(description = "调度策略")
    @NotBlank
    private String scheduleStrategy;

    @Schema(description = "调度配置信息")
    private String config;

    @Schema(description = "启用状态")
    @NotNull
    private Integer enableStatus;

    public void setConfig(Object scheduleConfig) {
        if (scheduleConfig == null) {
            return;
        }
        this.config = JsonUtils.toJsonString(scheduleConfig);
    }

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return null;
        }

        return JsonUtils.parseTree(this.config);
    }

    public ScheduleType getScheduleStrategy() {
        return ScheduleType.of(this.scheduleStrategy);
    }
}

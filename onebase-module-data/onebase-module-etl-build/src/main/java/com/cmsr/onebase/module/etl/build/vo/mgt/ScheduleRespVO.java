package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Schema(description = "数据工厂 - ETL - 调度配置响应VO")
@Data
public class ScheduleRespVO {

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "ETL ID")
    private Long workflowId;

    @Schema(description = "ETL名称")
    private String flowName;

    @Schema(description = "启用状态")
    private Integer enableStatus;

    @Schema(description = "调度策略")
    private String scheduleStrategy;

    @Schema(description = "调度配置")
    private String config;

    public JsonNode getConfig() {
        if (StringUtils.isBlank(this.config)) {
            return JsonUtils.createObjectNode();
        }
        return JsonUtils.parseTree(config);
    }
}

package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "数据工厂 - Etl - Etl详情VO")
@Data
public class WorkflowDetailVO {

    @Schema(description = "ETL ID")
    private Long id;

    @Schema(description = "ETL UUID")
    private String flowUuid;

    @Schema(description = "ETL名称")
    private String flowName;

    @Schema(description = "ETL详细配置")
    private String config;

    public JsonNode getConfig() {
        return JsonUtils.parseTree(this.config);
    }
}

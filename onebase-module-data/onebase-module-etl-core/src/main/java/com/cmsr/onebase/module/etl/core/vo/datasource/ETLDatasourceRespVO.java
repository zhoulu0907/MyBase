package com.cmsr.onebase.module.etl.core.vo.datasource;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "数据工厂 - 数据源创建/修改 VO")
@Data
public class ETLDatasourceRespVO {

    @Schema(description = "数据源ID")
    private Long id;

    @Schema(description = "应用ID")
    private Long applicationId;

    @Schema(description = "数据源编号")
    private String datasourceCode;

    @Schema(description = "数据源名称信息")
    private String datasourceName;

    @Schema(description = "数据源类型信息")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    private String config;

    @Schema(description = "只读")
    private Boolean readonly;

    @Schema(description = "采集状态")
    private String collectStatus;

    @Schema(description = "采集开始时间")
    private LocalDateTime collectStartTime;

    @Schema(description = "采集结束时间")
    private LocalDateTime collectEndTime;

    // TODO: lack of creator, create_time, updater, update_time.,

    public void setConfig(JsonNode config) {
        this.config = JsonUtils.toJsonString(config);
    }
}

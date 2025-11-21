package com.cmsr.onebase.module.etl.build.vo.datasource;

import com.cmsr.onebase.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

@Schema(description = "ETL - 数据源创建 Request VO")
@Data
public class ETLDatasourceCreateReqVO {

    @Schema(description = "应用ID")
    @NotNull(message = "应用ID不可为空")
    private Long applicationId;

    @Schema(description = "数据源编号")
    private String datasourceCode;

    @Schema(description = "数据源名称")
    @NotBlank(message = "数据源名称不能为空")
    @Size(min = 1, max = 200, message = "数据源名称最大长度为200字符")
    private String datasourceName;

    @Schema(description = "数据源描述")
    private String declaration;

    @Schema(description = "数据源类型信息")
    @NotBlank(message = "数据源类型不能为空")
    private String datasourceType;

    @Schema(description = "数据源配置信息")
    @NotBlank(message = "数据源配置信息不能为空")
    private String config;

    @Schema(description = "只读", defaultValue = "true")
    private Boolean readonly = true;

    @Schema(description = "是否创建时进行采集", defaultValue = "false")
    private Boolean withCollect = false;

    public JsonNode getConfig() {
        return JsonUtils.parseTree(this.config);
    }

    public Integer getReadonly() {
        return BooleanUtils.toInteger(readonly);
    }

    public void setWithCollect(Integer withCollect) {
        this.withCollect = BooleanUtils.toBoolean(withCollect);
    }

    public void setConfig(JsonNode config) {
        this.config = JsonUtils.toJsonString(config);
    }
}

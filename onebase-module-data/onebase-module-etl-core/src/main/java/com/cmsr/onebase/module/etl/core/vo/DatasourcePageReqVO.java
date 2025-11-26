package com.cmsr.onebase.module.etl.core.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "数据工厂 - 数据源 - 分页查询数据源请求VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasourcePageReqVO extends PageParam {

    @NotNull
    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long applicationId;

    @Schema(description = "数据源编号")
    private String datasourceUUID;

    @Schema(description = "数据源名称信息")
    private String datasourceName;

    @Schema(description = "数据源描述")
    private String declaration;

    @Schema(description = "数据源类型信息")
    private String datasourceType;

    @Schema(description = "只读")
    private Boolean readonly;

    @Schema(description = "采集状态")
    private String collectStatus;

}

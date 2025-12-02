package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 数据源分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class DatasourcePageReqVO extends PageParam {

    @Schema(description = "数据源名称", example = "用户数据库")
    private String datasourceName;

    @Schema(description = "数据源编码", example = "user_db")
    private String code;

    @Schema(description = "数据源类型", example = "POSTGRESQL")
    private String datasourceType;

    @Schema(description = "版本标识", example = "0")
    private Long versionTag;

    @Schema(description = "应用ID", example = "1")
    private String applicationId;

    @Schema(description = "数据源来源", example = "1")
    private Integer datasourceOrigin;

}

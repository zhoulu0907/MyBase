package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 数据源列表查询 Request VO
 *
 * @author matianyu
 * @date 2025-08-13
 */
@Schema(description = "管理后台 - 数据源列表查询 Request VO")
@Data
public class DatasourceListReqVO {

    @Schema(description = "应用ID", example = "545161214364356608")
    private String applicationId;

}

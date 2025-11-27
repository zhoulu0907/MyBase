package com.cmsr.onebase.module.etl.build.vo.mgt;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "数据工厂 - Etl - 创建Etl VO")
@Data
public class WorkflowCreateVO {

    @Schema(description = "应用ID")
    @NotNull
    private Long applicationId;

    @Schema(description = "ETL名称")
    @NotBlank(message = "ETL名称不能为空")
    private String flowName;

    @Schema(description = "ETL描述")
    private String declaration;

    @Schema(description = "ETL配置")
    @NotNull(message = "ETL配置不能为空")
    private JsonNode config;

}

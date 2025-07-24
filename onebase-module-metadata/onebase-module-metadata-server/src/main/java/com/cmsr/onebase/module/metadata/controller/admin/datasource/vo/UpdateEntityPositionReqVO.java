package com.cmsr.onebase.module.metadata.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 更新实体位置 Request VO")
@Data
public class UpdateEntityPositionReqVO {

    @Schema(description = "数据源ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "数据源ID不能为空")
    private Long datasourceId;

    @Schema(description = "实体编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "user")
    @NotBlank(message = "实体编码不能为空")
    private String entityCode;

    @Schema(description = "X坐标", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "X坐标不能为空")
    private Integer positionX;

    @Schema(description = "Y坐标", requiredMode = Schema.RequiredMode.REQUIRED, example = "200")
    @NotNull(message = "Y坐标不能为空")
    private Integer positionY;

}

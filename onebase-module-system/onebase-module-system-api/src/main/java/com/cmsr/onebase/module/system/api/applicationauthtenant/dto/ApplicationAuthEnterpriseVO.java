package com.cmsr.onebase.module.system.api.applicationauthtenant.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Schema(description = "应用授权企业表")
@Data

@ToString(callSuper = true)
public class ApplicationAuthEnterpriseVO   {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer applicationId;

    @Schema(description = "企业id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer enterpriseId;

    @Schema(description = "空间id", example = "200")
    private Integer tenantId;

    @Schema(description = "锁标识", example = "1")
    private Long lockVersion;
}
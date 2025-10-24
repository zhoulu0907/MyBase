package com.cmsr.onebase.module.system.api.applicationauthtenant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "应用授权企业表创建/修改 Request VO")
@Data
public class ApplicationAuthEnterpriseSaveReqVO {

    @Schema(description = "企业id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "企业id不能为空")
    private Long enterpriseId;

    @Schema(description = "应用id")
    @NotNull(message = "企业id list不能为空")
    private  List<Long> applicationIdList;
}
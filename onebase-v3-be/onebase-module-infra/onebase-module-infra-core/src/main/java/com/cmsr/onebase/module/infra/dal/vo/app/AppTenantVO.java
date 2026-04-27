package com.cmsr.onebase.module.infra.dal.vo.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "app  VO")
@Data
public class AppTenantVO {

    @Schema(description = "appId" )
    private Long appId;

    @Schema(description = "租户id", example = "")
    private Long tenantId;

}

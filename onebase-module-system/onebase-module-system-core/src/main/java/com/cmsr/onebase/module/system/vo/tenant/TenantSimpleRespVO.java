package com.cmsr.onebase.module.system.vo.tenant;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 租户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TenantSimpleRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "租户编码", example = "1")
    private String tenantCode;

    @Schema(description = "租户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    private String name;

    @Schema(description = "域名", example = "onebase")
    private String website;

    @Schema(description = "域名H5", example = "onebase")
    private String websiteH5;

    @Schema(description = "管理员id")
    private String adminUserId;
}

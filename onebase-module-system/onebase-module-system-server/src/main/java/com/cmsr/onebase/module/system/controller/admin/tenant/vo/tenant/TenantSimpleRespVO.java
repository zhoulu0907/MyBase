package com.cmsr.onebase.module.system.controller.admin.tenant.vo.tenant;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 租户 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TenantSimpleRespVO {

    @Schema(description = "租户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("租户编号")
    private Long id;

    @Schema(description = "租户编码", example = "1")
    @ExcelProperty("租户编码")
    private String tenantCode;

    @Schema(description = "租户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "onebase")
    @ExcelProperty("租户名")
    private String name;

    @Schema(description = "域名", example = "http://cmsr.com")
    private String website;

    @Schema(description = "域名H5", example = "http://h5.cmsr.com")
    private String websiteH5;
}

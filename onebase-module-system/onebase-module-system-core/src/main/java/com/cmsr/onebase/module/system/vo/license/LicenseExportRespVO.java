package com.cmsr.onebase.module.system.vo.license;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LicenseExportRespVO {

    @Schema(description = "企业名称(模糊)")
    private String enterpriseName;

    @Schema(description = "企业编号")
    private String enterpriseCode;

    @Schema(description = "企业地址")
    private String enterpriseAddress;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "租户数量限制")
    private String tenantLimit;

    @Schema(description = "用户数量限制")
    private String userLimit;

}

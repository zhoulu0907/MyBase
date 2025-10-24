package com.cmsr.onebase.module.system.api.enterprise.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnterpriseApplicationRespVO {
    @Schema(description = "应用名称")
    private String applicationName;
    @Schema(description = "应用编码")
    private String applicationCode;
    @Schema(description = "应用Id")
    private String applicationId;
    @Schema(description = "授权时间")
    private LocalDateTime authTime;
    @Schema(description = "版本号")
    private String versionNumber;
    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime expiresTime;

}

package com.cmsr.onebase.framework.common.biz.system.license.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 凭证 Response DTO
 */
@Schema(description = "RPC 服务 - 岗位 Response DTO")
@Data
public class LicenseRespDTO {

    @Schema(description = "企业名称(模糊)", requiredMode = Schema.RequiredMode.REQUIRED, example = "上海移动有限公司")
    private String enterpriseName;

    @Schema(description = "企业编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "F200090910001")
    private String enterpriseCode;

    @Schema(description = "企业地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "上海市浦东金桥开发区")
    private String enterpriseAddress;

    @Schema(description = "平台类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "私有化部署")
    private String platformType;

    @Schema(description = "到期时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-12-01 08:01:48")
    private LocalDateTime expireTime;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "enable")
    private String status;

    @Schema(description = "租户数量限制", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private String tenantLimit;

    @Schema(description = "用户数量限制", requiredMode = Schema.RequiredMode.REQUIRED, example = "300")
    private String userLimit;

}

package com.cmsr.onebase.module.system.vo.license;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * License 分页响应 VO
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Data
public class LicensePageRespVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "企业名称")
    private String enterpriseName;

    @Schema(description = "企业编号")
    private String enterpriseCode;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否为试用License")
    private Boolean isTrial;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "租户数量限制")
    private Integer tenantLimit;

    @Schema(description = "用户数量限制")
    private Integer userLimit;

}

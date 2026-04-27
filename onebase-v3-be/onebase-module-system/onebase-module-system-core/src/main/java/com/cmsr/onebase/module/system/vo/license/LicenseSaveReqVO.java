package com.cmsr.onebase.module.system.vo.license;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * License 创建/更新请求 VO
 *
 * @author matianyu
 * @date 2025-07-25
 */
@Data
public class LicenseSaveReqVO {

    @Schema(description = "主键ID", example = "1")
    private Long id;

    @Schema(description = "企业名称", required = true)
    private String enterpriseName;

    @Schema(description = "企业编号", required = true)
    private String enterpriseCode;

    @Schema(description = "企业地址")
    private String enterpriseAddress;

    @Schema(description = "平台类型")
    private String platformType;

    @Schema(description = "租户数量限制")
    @Positive(message = "用户上限必须大于零")
    private Integer tenantLimit;

    @Schema(description = "用户数量限制")
    @Positive(message = "用户上限必须大于零")
    private Integer userLimit;

    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "是否为试用License")
    private Boolean isTrial;

    @Schema(description = "License文件内容")
    private String licenseFile;

}

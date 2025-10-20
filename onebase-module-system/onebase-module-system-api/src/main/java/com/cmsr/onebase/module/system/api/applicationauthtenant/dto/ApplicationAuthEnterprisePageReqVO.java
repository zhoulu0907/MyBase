package com.cmsr.onebase.module.system.api.applicationauthtenant.dto;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "应用授权企业表分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationAuthEnterprisePageReqVO extends PageParam {

    @Schema(description = "应用id", example = "1")
    private Integer applicationId;

    @Schema(description = "企业id", example = "100")
    private Integer enterpriseId;

    @Schema(description = "空间id", example = "200")
    private Integer tenantId;
}
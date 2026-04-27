package com.cmsr.onebase.module.system.vo.corpapprelation;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "企业应用关联表")
@Data

@ToString(callSuper = true)
public class CorpAppRelationVO   {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long applicationId;

    @Schema(description = "应用编码" )
    private String applicationCode;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private String applicationName;

    @Schema(description = "版本号")
    private String versionNumber;

    @Schema(description = "授权时间")
    private LocalDateTime authorizationTime;

    @Schema(description = "过期时间")
    private LocalDateTime expiresTime;

    @Schema(description = "企业id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long corpId;

    @Schema(description = "状态")
    private Integer status;



}
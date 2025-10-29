package com.cmsr.onebase.module.system.api.corpapprelation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "企业应用关联创建/修改 Request VO")
@Data
public class CorpAppRelationInertReqVO {

    @Schema(description = "企业id", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "企业id不能为空")
    private Long corpId;

    @Schema(description = "应用id")
    @NotNull(message = "企业id list不能为空")
    private  List<Long> applicationIdList;


    @Schema(description = "授权时间")
    @NotNull(message = "授权时间")
    private LocalDateTime authorizationTime ;
}
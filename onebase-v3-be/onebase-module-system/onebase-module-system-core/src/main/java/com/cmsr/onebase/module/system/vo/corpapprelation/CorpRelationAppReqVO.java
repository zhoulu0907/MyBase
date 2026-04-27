package com.cmsr.onebase.module.system.vo.corpapprelation;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "企业关联应用")
@Data
public class CorpRelationAppReqVO {

    @Schema(description = "企业id")
    private  Long corpId;

    @Schema(description = "应用名称")
    private String appName ;
}
package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class CorpReqVO {
    @Schema(description = "企业Logo", example = "")
    private String corpLogo;

    @Schema(description = "企业名称", example = "")
    private String corpName;

    @Schema(description = "企业编号", example = "ALIBABA")
    private String corpCode;

    @Schema(description = "行业类型", example = "1")
    private Integer industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地址", example = "")
    private String address;

    @Schema(description = "用户数量", example = "100")
    private Integer userCount;


}

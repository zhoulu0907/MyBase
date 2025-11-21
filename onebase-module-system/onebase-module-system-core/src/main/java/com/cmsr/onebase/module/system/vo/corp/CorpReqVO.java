package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class CorpReqVO {
    @Schema(description = "企业Logo", example = "")
    private String corpLogo;

    @Schema(description = "企业名称", example = "")
    @NotBlank(message = "企业名称不能为空")
    private String corpName;

    @Schema(description = "企业编码", example = "ALIBABA")
    @NotBlank(message = "企业编码不能为空")
    private String corpCode;

    @Schema(description = "行业类型", example = "1")
    @NotNull(message = "行业类型不能为空")
    private Long industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地址", example = "")
    private String address;

    @Schema(description = "用户上限")
    @NotNull(message = "用户上限不能为空")
    private Integer userLimit;


}

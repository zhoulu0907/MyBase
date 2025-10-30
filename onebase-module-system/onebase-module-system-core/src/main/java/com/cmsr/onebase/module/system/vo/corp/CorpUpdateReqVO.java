package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CorpUpdateReqVO {
    @NotBlank(message = "企业id不能为空")
    @Schema(description = "企业ID", example = "")
    private Long id;

    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", example = "")
    private String corpName;

    @NotBlank(message = "企业Id不能为空")
    @Schema(description = "企业Id", example = "ALIBABA")
    private String corpId;

    @NotBlank(message = "行业类型不能为空")
    @Schema(description = "行业类型", example = "1")
    private Integer industryType;

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;

    @NotBlank(message = "地址不能为空")
    @Schema(description = "地址", example = "")
    private String address;

    @NotBlank(message = "用户数量上限不能为空")
    @Schema(description = "用户数量上限")
    private Integer userLimit;
}

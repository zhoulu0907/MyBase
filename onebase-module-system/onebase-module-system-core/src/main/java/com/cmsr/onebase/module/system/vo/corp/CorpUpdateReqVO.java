package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CorpUpdateReqVO {
    @NotNull(message = "企业id不能为空")
    @Schema(description = "企业ID", example = "")
    private Long id;

    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", example = "")
    private String corpName;

    @Schema(description = "企业编号")
    private String corpCode;

    @Schema(description = "行业类型", example = "1")
    private Long industryType;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "地址", example = "")
    private String address;

    @Schema(description = "用户数量上限")
    private Integer userLimit;

    @Schema(description = "企业logo", example = "")
    private String corpLogo;
}

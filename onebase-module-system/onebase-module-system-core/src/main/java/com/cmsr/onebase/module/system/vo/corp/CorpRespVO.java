package com.cmsr.onebase.module.system.vo.corp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 企业响应对象
 *
 * @author matianyu
 * @date 2025-08-20
 */
@Data
public class CorpRespVO {


    @NotBlank(message = "企业名称不能为空")
    @Schema(description = "企业名称", example = "")
    private String corpName;

    @NotBlank(message = "企业编号不能为空")
    @Schema(description = "企业编号", example = "ALIBABA")
    private String corpCode;

    @NotBlank(message = "行业类型不能为空")
    @Schema(description = "行业类型", example = "1")
    private Integer industryType;

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态", example = "1")
    private Integer status;

    @NotBlank(message = "地址不能为空")
    @Schema(description = "地址", example = "")
    private String address;

    @NotBlank(message = "用户数量不能为空")
    @Schema(description = "用户数量", example = "100")
    private Integer userCount;



}

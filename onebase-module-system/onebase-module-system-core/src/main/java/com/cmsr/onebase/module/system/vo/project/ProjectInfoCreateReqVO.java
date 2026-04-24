package com.cmsr.onebase.module.system.vo.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 项目创建 Request VO")
@Data
public class ProjectInfoCreateReqVO {

    @Schema(description = "项目编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PROJECT001")
    @NotBlank(message = "项目编码不能为空")
    @Size(max = 64, message = "项目编码长度不能超过64个字符")
    private String projectCode;

    @Schema(description = "项目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "研发项目")
    @NotBlank(message = "项目名称不能为空")
    @Size(max = 128, message = "项目名称长度不能超过128个字符")
    private String projectName;

    @Schema(description = "来源平台", example = "lingji")
    private String sourcePlatform;

    @Schema(description = "描述", example = "这是一个研发项目")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

    @Schema(description = "行业标签")
    private String industryTag;

    @Schema(description = "项目封面", example = "https://example.com/cover.png")
    private String projectCover;

    @Schema(description = "租户id", example = "1")
    private Long tenantId;


}
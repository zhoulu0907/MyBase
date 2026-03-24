package com.cmsr.onebase.module.system.vo.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "管理后台 - 项目更新 Request VO")
@Data
public class ProjectInfoUpdateReqVO {

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "项目ID不能为空")
    private Long id;

    @Schema(description = "项目名称", example = "研发项目")
    @Size(max = 128, message = "项目名称长度不能超过128个字符")
    private String projectName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "描述", example = "这是一个研发项目")
    @Size(max = 512, message = "描述长度不能超过512个字符")
    private String description;

}
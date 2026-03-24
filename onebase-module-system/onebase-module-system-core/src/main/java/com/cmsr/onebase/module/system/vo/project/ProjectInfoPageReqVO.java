package com.cmsr.onebase.module.system.vo.project;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 项目分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ProjectInfoPageReqVO extends PageParam {

    @Schema(description = "项目编码", example = "PROJECT001")
    private String projectCode;

    @Schema(description = "项目名称", example = "研发项目")
    private String projectName;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "来源平台", example = "lingji")
    private String sourcePlatform;

}
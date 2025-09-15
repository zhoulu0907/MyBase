package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 业务实体信息 Response VO")
@Data
public class BusinessEntityRespVO {

    @Schema(description = "实体名称", example = "用户表")
    private String entityName;

    @Schema(description = "实体编码", example = "user")
    private String entityCode;

    @Schema(description = "实体类型", example = "TABLE")
    private String entityType;

    @Schema(description = "实体描述", example = "系统用户信息表")
    private String description;

    @Schema(description = "字段列表")
    private List<EntityFieldRespVO> fields;

}

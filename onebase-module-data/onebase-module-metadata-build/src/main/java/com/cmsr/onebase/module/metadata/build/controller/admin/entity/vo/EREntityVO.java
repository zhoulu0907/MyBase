package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * ER图实体VO
 *
 * @author matianyu
 * @date 2025-08-07
 */
@Schema(description = "管理后台 - ER图实体")
@Data
public class EREntityVO {

    @Schema(description = "实体ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private String entityId;

    @Schema(description = "实体UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String entityUuid;

    @Schema(description = "实体名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户表")
    private String entityName;

    @Schema(description = "表名", requiredMode = Schema.RequiredMode.REQUIRED, example = "sys_user")
    private String tableName;

    @Schema(description = "实体描述", example = "系统用户信息表")
    private String description;

    @Schema(description = "实体类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "TABLE")
    private String entityType;

    @Schema(description = "实体编码", example = " code_001")
    private String code;

    @Schema(description = "前端展示配置", example = "code_001")
    private String displayConfig;

/*     @Schema(description = "实体坐标X", example = "100")
    private Integer positionX;

    @Schema(description = "实体坐标Y", example = "200")
    private Integer positionY; */

    @Schema(description = "字段列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ERFieldVO> fields;

    @Schema(description = "关系类型", example = "主表:PARENT，子表:CHILD")
    private String relationType;

    //todo
    @Schema(description = "状态", example = "1")
    private Integer status;
}

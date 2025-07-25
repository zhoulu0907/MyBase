package com.cmsr.onebase.module.metadata.controller.admin.datasource.vo;

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

    @Schema(description = "实体字段信息")
    @Data
    public static class EntityFieldRespVO {

        @Schema(description = "字段名称", example = "id")
        private String fieldName;

        @Schema(description = "字段类型", example = "BIGINT")
        private String fieldType;

        @Schema(description = "字段长度", example = "20")
        private Integer fieldLength;

        @Schema(description = "是否主键", example = "true")
        private Boolean isPrimaryKey;

        @Schema(description = "是否允许为空", example = "false")
        private Boolean isNullable;

        @Schema(description = "默认值", example = "")
        private String defaultValue;

        @Schema(description = "字段注释", example = "主键ID")
        private String comment;

    }

}

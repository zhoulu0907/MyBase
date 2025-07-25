package com.cmsr.onebase.module.metadata.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 数据源ER图信息 Response VO")
@Data
public class DatasourceErDiagramRespVO {

    @Schema(description = "数据源ID", example = "1024")
    private Long datasourceId;

    @Schema(description = "数据源名称", example = "用户数据库")
    private String datasourceName;

    @Schema(description = "业务实体列表")
    private List<ErEntityRespVO> entities;

    @Schema(description = "实体关系列表")
    private List<ErRelationRespVO> relations;

    @Schema(description = "ER图实体信息")
    @Data
    public static class ErEntityRespVO {

        @Schema(description = "实体名称", example = "用户表")
        private String entityName;

        @Schema(description = "实体编码", example = "user")
        private String entityCode;

        @Schema(description = "实体类型", example = "SYSTEM")
        private String entityType;

        @Schema(description = "实体描述", example = "系统用户信息表")
        private String description;

        @Schema(description = "字段列表")
        private List<BusinessEntityRespVO.EntityFieldRespVO> fields;

        @Schema(description = "画布位置X坐标", example = "100")
        private Integer positionX;

        @Schema(description = "画布位置Y坐标", example = "200")
        private Integer positionY;

    }

    @Schema(description = "ER图实体关系信息")
    @Data
    public static class ErRelationRespVO {

        @Schema(description = "关系名称", example = "用户订单关系")
        private String relationName;

        @Schema(description = "关系类型", example = "ONE_TO_MANY")
        private String relationType;

        @Schema(description = "源实体编码", example = "user")
        private String sourceEntityCode;

        @Schema(description = "目标实体编码", example = "order")
        private String targetEntityCode;

        @Schema(description = "源字段名", example = "id")
        private String sourceFieldName;

        @Schema(description = "目标字段名", example = "user_id")
        private String targetFieldName;

        @Schema(description = "级联操作类型", example = "READ")
        private String cascadeType;

    }

}

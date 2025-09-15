package com.cmsr.onebase.module.metadata.build.controller.admin.datasource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - ER图实体关系信息 Response VO")
@Data
public class ErRelationRespVO {

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

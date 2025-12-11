package com.cmsr.onebase.module.metadata.runtime.controller.app.relationship.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 运行态 - 实体及其关联子表信息 Response VO
 *
 * @author matianyu
 * @date 2025-12-04
 */
@Schema(description = "运行态 - 实体及其关联子表信息 Response VO")
@Data
public class EntityWithChildrenRespVO {

    @Schema(description = "实体ID", example = "1001")
    private Long entityId;

    @Schema(description = "实体UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private String entityUuid;

    @Schema(description = "实体显示名称", example = "用户信息")
    private String entityName;

    @Schema(description = "实体编码", example = "ABC")
    private String entityCode;

    @Schema(description = "实际表名", example = "user_info")
    private String tableName;

    @Schema(description = "父表字段信息列表")
    private List<EntityFieldInfoRespVO> parentFields;

    @Schema(description = "关联的子表信息列表")
    private List<ChildEntityInfoRespVO> childEntities;

}

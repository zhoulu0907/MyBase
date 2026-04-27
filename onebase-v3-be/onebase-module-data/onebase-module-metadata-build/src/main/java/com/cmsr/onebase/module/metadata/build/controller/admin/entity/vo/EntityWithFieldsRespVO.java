package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 实体及字段信息 Response VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "管理后台 - 实体及字段信息 Response VO")
@Data
public class EntityWithFieldsRespVO {

    @Schema(description = "实体ID", example = "1001")
    private Long entityId;

    @Schema(description = "实体UUID", example = "01onal1s-0000-0000-0000-000000000001")
    private String entityUuid;

    @Schema(description = "实体显示名称", example = "用户信息")
    private String entityName;

    @Schema(description = "实体编码", example = "user_info")
    private String entityCode;

    @Schema(description = "实际表名", example = "t_user_info")
    private String tableName;

    @Schema(description = "实体字段列表（完整字段信息）")
    private List<EntityFieldRespVO> fields;

    @Schema(description = "关联的子表信息列表")
    private List<ChildEntityWithFieldsRespVO> childEntities;
}

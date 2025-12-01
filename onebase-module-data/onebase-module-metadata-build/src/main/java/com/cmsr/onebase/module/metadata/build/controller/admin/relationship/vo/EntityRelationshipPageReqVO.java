package com.cmsr.onebase.module.metadata.build.controller.admin.relationship.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理后台 - 实体关系分页 Request VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 实体关系分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityRelationshipPageReqVO extends PageParam {

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12345")
    private String applicationId;

    @Schema(description = "实体ID（查询与该实体相关的所有关系，无论作为源实体还是目标实体）", example = "544472937227489280")
    private String entityId;

    @Schema(description = "源实体ID", example = "2001")
    private String sourceEntityId;

    @Schema(description = "目标实体ID", example = "2002")
    private String targetEntityId;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

}

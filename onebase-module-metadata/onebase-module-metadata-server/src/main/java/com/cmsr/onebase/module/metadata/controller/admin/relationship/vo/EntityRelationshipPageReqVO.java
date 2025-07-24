package com.cmsr.onebase.module.metadata.controller.admin.relationship.vo;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 实体关系分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityRelationshipPageReqVO extends PageParam {

    @Schema(description = "关系名称", example = "用户部门关系")
    private String relationName;

    @Schema(description = "源实体ID", example = "1")
    private Long sourceEntityId;

    @Schema(description = "目标实体ID", example = "2")
    private Long targetEntityId;

    @Schema(description = "关系类型", example = "ONE_TO_MANY")
    private String relationshipType;

    @Schema(description = "运行模式", example = "0")
    private Integer runMode;

    @Schema(description = "应用ID", example = "1")
    private Long appId;

}

package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 运行态 - 数据选择配置 VO
 *
 * @author matianyu
 * @date 2025-12-15
 */
@Schema(description = "运行态 - 数据选择配置 VO")
@Data
public class DataSelectionConfig {

    @Schema(description = "关联关系主键id", example = "1001")
    private Long relationId;

    @Schema(description = "关联的目标实体ID", example = "2001")
    private Long targetEntityId;

    @Schema(description = "关联的目标实体UUID", example = "01onal1s-0000-0000-0000-000000000002")
    private String targetEntityUuid;

    @Schema(description = "关联的目标字段ID", example = "3001")
    private Long targetFieldId;

    @Schema(description = "关联的目标字段UUID", example = "01onal1s-0000-0000-0000-000000000003")
    private String targetFieldUuid;
}

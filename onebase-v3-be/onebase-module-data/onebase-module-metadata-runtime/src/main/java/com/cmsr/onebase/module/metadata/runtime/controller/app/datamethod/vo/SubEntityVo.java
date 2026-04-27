package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * SubEntityVo
 *
 * @author biantianyu
 * @since 2025/10/22
 */
@Data
@Schema(description = "子表VO")
public class SubEntityVo {
    //    @Schema(description = "子实体ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long subEntityId;
    //    @Schema(description = "子实体数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Map<Long, Object>> subData;
}

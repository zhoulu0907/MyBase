package com.cmsr.onebase.module.metadata.runtime.controller.app.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 动态数据响应VO
 *
 * @author bty418
 * @date 2025-09-10
 */
@Data
@Schema(description = "动态数据响应VO")
public class DynamicDataRespVO {

    @Schema(description = "实体ID")
    private Long entityId;

    @Schema(description = "实体名称")
    private String entityName;

    @Schema(description = "数据内容")
    private Map<String, Object> data;

    @Schema(description = "字段类型信息")
    private Map<String, String> fieldType;

    @Schema(description = "子表数据对象")
    private List<SubEntityVo> subEntities;
}

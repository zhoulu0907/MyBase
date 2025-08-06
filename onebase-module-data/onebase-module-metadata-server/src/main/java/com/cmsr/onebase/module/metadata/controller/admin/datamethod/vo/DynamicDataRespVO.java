package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 动态数据响应VO
 *
 * @author matianyu
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 动态数据响应 VO")
@Data
public class DynamicDataRespVO {

    @Schema(description = "数据内容，key为字段名，value为字段值")
    private Map<String, Object> data;

    @Schema(description = "实体ID", example = "1")
    private String entityId;

    @Schema(description = "实体名称", example = "用户")
    private String entityName;

}

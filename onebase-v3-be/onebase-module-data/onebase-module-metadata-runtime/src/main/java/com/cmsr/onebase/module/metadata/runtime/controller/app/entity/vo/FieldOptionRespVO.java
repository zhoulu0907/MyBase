package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段选项 响应 VO
 *
 * @author bty418
 * @date 2025-10-30
 */
@Data
public class FieldOptionRespVO {
    
    @Schema(description = "选项ID")
    private String id;
    
    @Schema(description = "选项UUID")
    private String optionUuid;
    
    @Schema(description = "字段UUID")
    private String fieldUuid;
    
    @Schema(description = "显示名称")
    private String optionLabel;
    
    @Schema(description = "选项值")
    private String optionValue;
    
    @Schema(description = "排序")
    private Integer optionOrder;
    
    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;
    
    @Schema(description = "描述")
    private String description;
}


package com.cmsr.onebase.module.metadata.runtime.controller.app.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排序项
 *
 * @author bty418
 * @date 2025-10-30
 */
@Data
public class SortItemVO {
    
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "选项排序")
    private Integer optionOrder;
}


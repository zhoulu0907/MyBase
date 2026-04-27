package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排序项
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
public class SortItemDTO {
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "选项排序")
    private Integer optionOrder;
}

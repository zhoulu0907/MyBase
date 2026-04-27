package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字段详情中的自动编号摘要 VO
 */
@Data
public class EntityFieldAutoNumberBriefRespVO {
    @Schema(description = "是否启用：0-启用，1-禁用")
    private Integer enabled;
    @Schema(description = "编号模式：NATURAL/FIXED_DIGITS")
    private String mode;
    @Schema(description = "指定位数")
    private Integer digitWidth;
    @Schema(description = "重置周期：NONE/DAILY/MONTHLY/YEARLY")
    private String resetCycle;
}



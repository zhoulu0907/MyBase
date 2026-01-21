package com.cmsr.onebase.module.metadata.api.datasource.dto.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 自动编号配置导出DTO
 * 对应表: metadata_auto_number_config
 *
 * @author bty418
 * @date 2026-01-21
 */
@Schema(description = "自动编号配置导出DTO")
@Data
public class MetadataAutoNumberConfigExportDTO {

    @Schema(description = "配置UUID")
    private String configUuid;

    @Schema(description = "字段UUID")
    private String fieldUuid;

    @Schema(description = "编号模式")
    private String numberMode;

    @Schema(description = "数字宽度")
    private Short digitWidth;

    @Schema(description = "溢出后是否继续")
    private Integer overflowContinue;

    @Schema(description = "初始值")
    private Long initialValue;

    @Schema(description = "重置周期")
    private String resetCycle;

    @Schema(description = "下一条记录以修改后的开始值编号：1-是，0-否")
    private Integer resetOnInitialChange;

    @Schema(description = "是否启用：1-启用，0-禁用")
    private Integer isEnabled;

    @Schema(description = "SEQUENCE规则项在列表中的排序位置")
    private Integer sequenceOrder;
}

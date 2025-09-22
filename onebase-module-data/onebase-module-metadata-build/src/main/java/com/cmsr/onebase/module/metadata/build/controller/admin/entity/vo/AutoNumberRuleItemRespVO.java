package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 自动编号规则项响应VO
 *
 * @author bty418
 * @date 2025-09-17
 */
@Data
@Schema(description = "自动编号规则项响应VO")
public class AutoNumberRuleItemRespVO {

    @Schema(description = "规则项ID", example = "1")
    private Long id;

    @Schema(description = "配置ID", example = "1")
    private Long configId;

    @Schema(description = "规则项类型", example = "SEQUENCE")
    private String itemType;

    @Schema(description = "排序序号", example = "1")
    private Integer itemOrder;

    @Schema(description = "格式化规则", example = "yyyyMMdd")
    private String format;

    @Schema(description = "固定文本值", example = "ORDER")
    private String textValue;

    @Schema(description = "引用字段ID", example = "123")
    private Long refFieldId;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "应用ID", example = "1")
    private Long appId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
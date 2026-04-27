package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @Schema(description = "配置UUID", example = "01onal1s-0000-0000-0000-000000000005")
    private String configUuid;

    @Schema(description = "规则项类型", example = "SEQUENCE")
    private String itemType;

    @Schema(description = "排序序号", example = "1")
    private Integer itemOrder;

    @Schema(description = "格式化规则", example = "yyyyMMdd")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String format;

    @Schema(description = "固定文本值", example = "ORDER")
    private String textValue;

    @Schema(description = "引用字段ID", example = "123")
    private Long refFieldId;

    @Schema(description = "引用字段UUID", example = "01onal1s-0000-0000-0000-000000000006")
    private String refFieldUuid;

    @Schema(description = "是否启用(1-启用, 0-禁用)", example = "1")
    private Integer isEnabled;

    @Schema(description = "应用ID", example = "1")
    private Long applicationId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    /**
     * 兼容性方法：为TEXT类型的规则项提供format字段
     * 当itemType为TEXT时，返回textValue作为format值以保持兼容性
     */
    @JsonProperty("format")
    public String getFormatForCompatibility() {
        if ("TEXT".equalsIgnoreCase(this.itemType) && this.textValue != null) {
            return this.textValue;
        }
        return this.format;
    }
}
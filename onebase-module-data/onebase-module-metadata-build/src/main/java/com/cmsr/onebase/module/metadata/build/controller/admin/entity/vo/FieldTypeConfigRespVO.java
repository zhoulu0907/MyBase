package com.cmsr.onebase.module.metadata.build.controller.admin.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理后台 - 字段类型配置 Response VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 字段类型配置 Response VO")
@Data
public class FieldTypeConfigRespVO {

    @Schema(description = "字段类型编码", example = "VARCHAR")
    private String fieldType;

    @Schema(description = "显示名称", example = "短文本")
    private String displayName;

    @Schema(description = "分类", example = "TEXT")
    private String category;

    @Schema(description = "是否支持长度设置", example = "true")
    private Boolean supportLength;

    @Schema(description = "是否支持小数位设置", example = "false")
    private Boolean supportDecimal;

    @Schema(description = "默认长度", example = "255")
    private Integer defaultLength;

    @Schema(description = "最大长度", example = "4000")
    private Integer maxLength;

    @Schema(description = "默认小数位数", example = "2")
    private Integer defaultDecimal;

}

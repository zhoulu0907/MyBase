package com.cmsr.onebase.module.metadata.controller.admin.datamethod.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 管理后台 - 数据方法详情参数 VO
 *
 * @author bty418
 * @date 2025-01-25
 */
@Schema(description = "管理后台 - 数据方法详情参数 VO")
@Data
public class DataMethodDetailParameterVO {

    @Schema(description = "参数名称", example = "username")
    private String paramName;

    @Schema(description = "参数类型", example = "VARCHAR")
    private String paramType;

    @Schema(description = "参数长度", example = "50")
    private Integer paramLength;

    @Schema(description = "是否必填", example = "true")
    private Boolean required;

    @Schema(description = "参数描述", example = "用户名")
    private String description;

    @Schema(description = "校验规则列表", example = "[\"长度4-20位\", \"只能包含字母、数字和下划线\"]")
    private List<String> validationRules;

} 
package com.cmsr.onebase.module.build.controller.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/8/7 16:50
 */
@Data
@Schema(description = "应用管理 - 数据 Filter VO")
public class AuthDataFilterVO {

    @Schema(description = "主键Id")
    private Long id;

    @Schema(description = "条件组")
    private Integer conditionGroup;

    @Schema(description = "条件顺序")
    private Integer conditionOrder;

    @Schema(description = "字段id")
    private Long fieldId;

    @Schema(description = "比较操作符号")
    private String fieldOperator;

    @Schema(description = "字段值类型")
    private String fieldValueType;

    @Schema(description = "字段值")
    private String fieldValue;


}

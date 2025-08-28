package com.cmsr.onebase.module.formula.api.function.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 函数 Response DTO
 *
 * @author matianyu
 * @date 2025-08-28
 */
@Schema(description = "RPC 服务 - 函数信息 Response DTO")
@Data
public class FunctionRespDTO {

    @Schema(description = "函数编号", example = "1024")
    private Long id;

    @Schema(description = "函数类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "BUILT_IN")
    private String type;

    @Schema(description = "函数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUM")
    private String name;

    @Schema(description = "函数表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUM({0})")
    private String expression;

    @Schema(description = "函数简介", example = "求和函数")
    private String summary;

    @Schema(description = "函数用法", example = "用于计算数值的总和")
    private String usage;

    @Schema(description = "函数示例", example = "SUM(1,2,3) = 6")
    private String example;

    @Schema(description = "返回值类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "NUMBER")
    private String returnType;

    @Schema(description = "函数版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    private String version;

    @Schema(description = "函数状态，见 CommonStatusEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "时间戳格式")
    private LocalDateTime updateTime;

}

package com.cmsr.onebase.module.formula.vo.function;

import com.cmsr.onebase.framework.common.enums.CommonStatusEnum;
import com.cmsr.onebase.framework.common.enums.FunctionTypeEnum;
import com.cmsr.onebase.framework.common.enums.ReturnTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "公式引擎 - 函数创建/修改 Request VO")
@Data
public class FunctionUpdateReqVO {

    @Schema(description = "函数编号", example = "1024")
    private Long id;

    @Schema(description = "函数类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "BUILT_IN")
    @NotBlank(message = "函数类型不能为空")
    @InEnum(value = FunctionTypeEnum.class, message = "函数类型必须是 {value}")
    @Size(max = 64, message = "函数类型长度不能超过 64 个字符")
    private String type;

    @Schema(description = "函数名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUM")
    @NotBlank(message = "函数名称不能为空")
    @Size(max = 128, message = "函数名称长度不能超过 128 个字符")
    private String name;

    @Schema(description = "函数表达式", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUM({0})")
    @NotBlank(message = "函数表达式不能为空")
    private String expression;

    @Schema(description = "函数简介", example = "求和函数")
    @Size(max = 512, message = "函数简介长度不能超过 512 个字符")
    private String summary;

    @Schema(description = "函数用法", example = "用于计算数值的总和")
    private String usage;

    @Schema(description = "函数示例", example = "SUM(1,2,3) = 6")
    private String example;

    @Schema(description = "返回值类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "NUMBER")
    @NotBlank(message = "返回值类型不能为空")
    @InEnum(value = ReturnTypeEnum.class, message = "返回值类型必须是 {value}")
    @Size(max = 64, message = "返回值类型长度不能超过 64 个字符")
    private String returnType;

    @Schema(description = "函数版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    @NotBlank(message = "函数版本不能为空")
    @Size(max = 32, message = "函数版本长度不能超过 32 个字符")
    private String version;

    @Schema(description = "函数状态，见 CommonStatusEnum 枚举", example = "1")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}

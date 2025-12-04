package com.cmsr.onebase.module.formula.vo.function;

import com.cmsr.onebase.framework.common.enums.FunctionTypeEnum;
import com.cmsr.onebase.framework.common.validation.InEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "公式引擎 - 函数列表 Request VO")
@Data
public class FunctionListReqVO {

    @Schema(description = "函数类型", example = "BUILT_IN")
    @InEnum(value = FunctionTypeEnum.class,message = "函数类型必须为{value}")
    private String type;

    @Schema(description = "函数名称，模糊匹配", example = "SUM")
    private String name;

    @Schema(description = "函数状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}

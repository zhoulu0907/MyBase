package com.cmsr.onebase.module.formula.vo.function;

import com.cmsr.onebase.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "公式引擎 - 函数分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class FunctionPageReqVO extends PageParam {

    @Schema(description = "函数类型", example = "BUILT_IN")
    private String type;

    @Schema(description = "函数名称，模糊匹配", example = "SUM")
    private String name;

    @Schema(description = "函数状态，参见 CommonStatusEnum 枚举类", example = "1")
    private Integer status;

}

package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-状态更新请求")
public class CustomButtonStatusReqVO {

    @NotNull(message = "按钮ID不能为空")
    @Schema(description = "按钮ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @NotBlank(message = "状态不能为空")
    @Schema(description = "状态：ENABLE/DISABLE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;
}

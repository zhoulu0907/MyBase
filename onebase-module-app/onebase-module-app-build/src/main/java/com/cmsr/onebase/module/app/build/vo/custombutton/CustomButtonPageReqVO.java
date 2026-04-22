package com.cmsr.onebase.module.app.build.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "自定义按钮-分页查询请求")
public class CustomButtonPageReqVO {

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;
}

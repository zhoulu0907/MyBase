package com.cmsr.onebase.module.app.runtime.vo.custombutton;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "运行态-自定义按钮批量执行请求")
public class RuntimeCustomButtonBatchExecuteReqVO {

    @NotBlank(message = "按钮编码不能为空")
    @Schema(description = "按钮编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String buttonCode;

    @NotNull(message = "页面集ID不能为空")
    @Schema(description = "页面集ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSetId;

    @Schema(description = "菜单ID")
    private Long menuId;

    @Schema(description = "页面ID")
    private Long pageId;

    @NotEmpty(message = "记录ID列表不能为空")
    @Schema(description = "记录ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> recordIds;
}

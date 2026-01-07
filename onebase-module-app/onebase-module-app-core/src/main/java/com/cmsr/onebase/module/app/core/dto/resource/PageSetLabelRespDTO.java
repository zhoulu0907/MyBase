package com.cmsr.onebase.module.app.core.dto.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageSetLabelRespDTO {

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "标签ID不能为空")
    private Long id;

    @Schema(description = "页面集id", requiredMode = Schema.RequiredMode.REQUIRED, example = "home_set")
    @NotNull(message = "页面集id不能为空")
    private Long pageSetId;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "标签名称")
    @NotNull(message = "标签名称不能为空")
    private String labelName;

    @Schema(description = "标签值", requiredMode = Schema.RequiredMode.REQUIRED, example = "标签值")
    @NotNull(message = "标签值不能为空")
    private String labelValue;

}

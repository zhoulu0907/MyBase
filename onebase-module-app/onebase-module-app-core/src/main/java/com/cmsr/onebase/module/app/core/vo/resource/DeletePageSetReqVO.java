package com.cmsr.onebase.module.app.core.vo.resource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeletePageSetReqVO {

    @Schema(description = "菜单id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单id不能为空")
    private Long menuId;
}

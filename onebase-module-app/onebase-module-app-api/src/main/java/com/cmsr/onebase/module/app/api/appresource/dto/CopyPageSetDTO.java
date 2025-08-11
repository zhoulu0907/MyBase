package com.cmsr.onebase.module.app.api.appresource.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "RPC 服务 - CopyPageSet DTO")
@Data
public class CopyPageSetDTO {

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单ID不能为空")
    private Long menuId;

    @Schema(description = "复制后的新菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "复制后的新菜单ID不能为空")
    private Long newMenuId;

}

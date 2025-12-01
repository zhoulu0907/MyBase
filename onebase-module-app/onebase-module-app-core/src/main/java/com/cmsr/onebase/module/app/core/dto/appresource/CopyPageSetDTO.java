package com.cmsr.onebase.module.app.core.dto.appresource;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "RPC 服务 - CopyPageSet DTO")
@Data
public class CopyPageSetDTO {

    @Schema(description = "菜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "菜单ID不能为空")
    private String menuUuid;

    @Schema(description = "复制后的新菜单编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "复制后的新菜单编码不能为空")
    private String newMenuUuid;

}

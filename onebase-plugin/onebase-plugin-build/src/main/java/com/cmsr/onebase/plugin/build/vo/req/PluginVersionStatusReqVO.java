package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 插件版本状态变更请求 VO
 *
 * @author matianyu
 * @date 2026-01-07
 */
@Schema(description = "管理后台 - 插件版本状态变更 Request VO")
@Data
public class PluginVersionStatusReqVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-plugin")
    @NotBlank(message = "插件ID不能为空")
    private String pluginId;

    @Schema(description = "插件版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    @NotBlank(message = "插件版本不能为空")
    private String pluginVersion;

}

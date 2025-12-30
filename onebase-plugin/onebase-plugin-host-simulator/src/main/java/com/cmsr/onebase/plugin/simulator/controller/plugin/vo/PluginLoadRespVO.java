package com.cmsr.onebase.plugin.simulator.controller.plugin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件加载结果 Response VO
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Schema(description = "插件管理 - 加载结果 Response VO")
@Data
public class PluginLoadRespVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "demo-plugin")
    private String pluginId;

    @Schema(description = "插件状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "LOADED")
    private String state;

    @Schema(description = "插件路径", example = "/plugins/demo-plugin-1.0.0.zip")
    private String pluginPath;
}

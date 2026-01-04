package com.cmsr.onebase.plugin.simulator.controller.plugin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件信息 Response VO
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Schema(description = "插件管理 - 插件信息 Response VO")
@Data
public class PluginRespVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "demo-plugin")
    private String pluginId;

    @Schema(description = "插件描述", example = "演示插件")
    private String description;

    @Schema(description = "插件版本", example = "1.0.0")
    private String version;

    @Schema(description = "插件提供者", example = "OneBase Team")
    private String provider;

    @Schema(description = "插件状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "STARTED")
    private String state;

    @Schema(description = "插件类名", example = "com.cmsr.onebase.plugin.demo.DemoPlugin")
    private String pluginClass;

    @Schema(description = "插件路径", example = "/path/to/plugin.zip")
    private String pluginPath;

    @Schema(description = "HTTP路由数量", example = "5")
    private Integer httpRouteCount;
}

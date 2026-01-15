package com.cmsr.onebase.plugin.simulator.controller.plugin.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件操作结果项 Response VO
 *
 * @author chengyuansen
 * @date 2025-12-17
 */
@Schema(description = "插件管理 - 操作结果项 Response VO")
@Data
public class PluginOperationResultItemVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "demo-plugin")
    private String pluginId;

    @Schema(description = "是否成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean success;

    @Schema(description = "操作后状态", example = "STARTED")
    private String state;

    @Schema(description = "错误消息（失败时）", example = "Plugin not found")
    private String error;

    public static PluginOperationResultItemVO success(String pluginId, String state) {
        PluginOperationResultItemVO item = new PluginOperationResultItemVO();
        item.setPluginId(pluginId);
        item.setSuccess(true);
        item.setState(state);
        return item;
    }

    public static PluginOperationResultItemVO error(String pluginId, String error) {
        PluginOperationResultItemVO item = new PluginOperationResultItemVO();
        item.setPluginId(pluginId);
        item.setSuccess(false);
        item.setError(error);
        return item;
    }
}

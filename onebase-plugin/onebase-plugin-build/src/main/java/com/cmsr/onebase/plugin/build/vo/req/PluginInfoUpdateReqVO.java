package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 插件基础信息更新请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件基础信息更新 Request VO")
@Data
public class PluginInfoUpdateReqVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "插件ID不能为空")
    private Long pluginId;

    @Schema(description = "插件名称", example = "示例插件")
    private String pluginName;

    @Schema(description = "插件描述", example = "这是一个示例插件")
    private String pluginDescription;

    @Schema(description = "插件图标文件ID", example = "2048")
    private Long pluginIcon;

}

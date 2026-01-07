package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 插件列表响应 VO（聚合展示）
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件列表 Response VO")
@Data
public class PluginInfoRespVO {

    @Schema(description = "当前版本记录ID", example = "1024")
    private Long id;

    @Schema(description = "插件唯一标识", example = "100")
    private String pluginId;

    @Schema(description = "插件名称", example = "示例插件")
    private String pluginName;

    @Schema(description = "插件图标ID", example = "2048")
    private Long pluginIcon;

    @Schema(description = "插件描述", example = "这是一个示例插件")
    private String pluginDescription;

    @Schema(description = "当前版本号", example = "1.0.0")
    private String pluginVersion;

    @Schema(description = "当前状态（0停用，1启用）", example = "1")
    private Integer status;

    @Schema(description = "版本总数", example = "3")
    private Integer versionCount;

    @Schema(description = "首次创建时间")
    private LocalDateTime createTime;

    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

}

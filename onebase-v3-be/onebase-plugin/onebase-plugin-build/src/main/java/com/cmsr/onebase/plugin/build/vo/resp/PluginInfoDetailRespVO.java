package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 插件详情响应 VO（含版本列表）
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件详情 Response VO")
@Data
public class PluginInfoDetailRespVO {

    @Schema(description = "插件唯一标识", example = "100")
    private String pluginId;

    @Schema(description = "插件名称", example = "示例插件")
    private String pluginName;

    @Schema(description = "插件图标", example = "el-icon-user")
    private String pluginIcon;

    @Schema(description = "插件描述", example = "这是一个示例插件")
    private String pluginDescription;

    @Schema(description = "插件元数据信息")
    private String pluginMetaInfo;

    @Schema(description = "版本列表")
    private List<PluginVersionRespVO> versions;

    @Schema(description = "首次创建时间")
    private LocalDateTime createTime;

}

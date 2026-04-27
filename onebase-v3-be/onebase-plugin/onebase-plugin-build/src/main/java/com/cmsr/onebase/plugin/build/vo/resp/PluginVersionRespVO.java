package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 插件版本响应 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件版本 Response VO")
@Data
public class PluginVersionRespVO {

    @Schema(description = "版本记录ID", example = "1024")
    private Long id;

    @Schema(description = "版本号", example = "1.0.0")
    private String pluginVersion;

    @Schema(description = "版本描述", example = "新增xxx功能")
    private String pluginVersionDescription;

    @Schema(description = "安装包文件ID", example = "3072")
    private Long pluginPackage;

    @Schema(description = "状态（0停用，1启用）", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

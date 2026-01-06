package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件包信息响应 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件包信息 Response VO")
@Data
public class PluginPackageRespVO {

    @Schema(description = "记录ID", example = "1024")
    private Long id;

    @Schema(description = "包名称", example = "plugin-backend.jar")
    private String packageName;

    @Schema(description = "包类型（0前端，1后端）", example = "1")
    private Integer packageType;

}

package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 插件配置响应 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件配置 Response VO")
@Data
public class PluginConfigRespVO {

    @Schema(description = "配置ID", example = "1024")
    private Long id;

    @Schema(description = "配置键", example = "api.url")
    private String configKey;

    @Schema(description = "配置值", example = "http://localhost:8080")
    private String configValue;

    @Schema(description = "值类型（normal普通，secret密文）", example = "normal")
    private String valueType;

}

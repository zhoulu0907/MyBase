package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 插件配置更新请求 VO
 *
 * @author matianyu
 * @date 2026-01-06
 */
@Schema(description = "管理后台 - 插件配置更新 Request VO")
@Data
public class PluginConfigUpdateReqVO {

    @Schema(description = "配置ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "配置ID不能为空")
    private Long id;

    @Schema(description = "配置值", example = "config_value")
    private String configValue;

}

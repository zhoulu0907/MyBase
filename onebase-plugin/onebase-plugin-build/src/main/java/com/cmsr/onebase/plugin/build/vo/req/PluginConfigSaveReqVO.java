package com.cmsr.onebase.plugin.build.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 插件配置保存请求 VO
 *
 * @author matianyu
 * @date 2026-01-07
 */
@Schema(description = "管理后台 - 插件配置保存 Request VO")
@Data
public class PluginConfigSaveReqVO {

    @Schema(description = "插件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test-plugin")
    @NotBlank(message = "插件ID不能为空")
    private String pluginId;

    @Schema(description = "插件版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0.0")
    @NotBlank(message = "插件版本不能为空")
    private String pluginVersion;

    @Schema(description = "配置项Map，key为configKey，value为配置值对象", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "配置项不能为空")
    private Map<String, ConfigValueVO> configs;

    /**
     * 配置值对象
     */
    @Data
    @Schema(description = "配置值对象")
    public static class ConfigValueVO {

        @Schema(description = "配置值", example = "http://localhost:8080")
        private String configValue;

        @Schema(description = "值类型（normal普通，secret密文）", example = "normal")
        private String valueType;

    }

}

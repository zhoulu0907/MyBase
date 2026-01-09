package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 插件配置模板响应 VO（从plugin_info的plugin_config_info字段获取）
 *
 * @author matianyu
 * @date 2026-01-07
 */
@Schema(description = "管理后台 - 插件配置模板 Response VO")
@Data
public class PluginConfigTemplateRespVO {

    @Schema(description = "配置模板列表")
    private List<ConfigTemplateItem> configTemplates;

    /**
     * 配置模板项
     */
    @Data
    @Schema(description = "配置模板项")
    public static class ConfigTemplateItem {

        @Schema(description = "配置键", example = "api.url")
        private String configKey;

        @Schema(description = "配置名称", example = "API地址")
        private String configName;

        @Schema(description = "配置描述", example = "后端API服务地址")
        private String description;

        @Schema(description = "默认值", example = "http://localhost:8080")
        private String defaultValue;

        @Schema(description = "值类型（normal普通，secret密文）", example = "normal")
        private String valueType;

        @Schema(description = "是否必填", example = "true")
        private Boolean required;

    }

}

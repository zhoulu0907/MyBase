package com.cmsr.onebase.plugin.build.vo.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 插件配置详情响应 VO（Map结构，key为configKey，value为配置值对象）
 *
 * @author matianyu
 * @date 2026-01-07
 */
@Schema(description = "管理后台 - 插件配置详情 Response VO")
@Data
public class PluginConfigDetailRespVO {

    @Schema(description = "配置项Map，key为configKey，value为配置值对象")
    private Map<String, ConfigValueItem> configs;

    /**
     * 配置值项
     */
    @Data
    @Schema(description = "配置值项")
    public static class ConfigValueItem {

        @Schema(description = "配置值", example = "http://localhost:8080")
        private String configValue;

        @Schema(description = "值类型（normal普通，secret密文）", example = "normal")
        private String valueType;

    }

}

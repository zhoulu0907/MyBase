package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新动作请求VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新动作请求VO")
public class UpdateActionReqVO {

    @Schema(description = "动作名称", example = "获取用户信息")
    private String actionName;

    @Schema(description = "动作描述", example = "通过HTTP GET获取用户信息")
    private String description;

    @Schema(description = "基础信息配置")
    private JsonNode basicInfo;

    @Schema(description = "入参配置")
    private JsonNode inputConfig;

    @Schema(description = "出参配置")
    private JsonNode outputConfig;

    @Schema(description = "调试配置")
    private JsonNode debugConfig;
}

package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 保存动作请求VO
 *
 * @author kanten
 * @since 2026-01-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "保存动作请求VO")
public class SaveActionReqVO {

    @Schema(description = "动作名称", example = "获取用户信息", required = true)
    @NotBlank(message = "动作名称不能为空")
    private String actionName;

    @Schema(description = "动作编码", example = "GET_USER", required = true)
    @NotBlank(message = "动作编码不能为空")
    private String actionCode;

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

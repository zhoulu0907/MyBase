package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 更新连接器动作请求VO
 *
 * @author onebase
 * @since 2026-01-26
 */
@Data
@Schema(description = "更新连接器动作请求")
public class UpdateConnectorActionReqVO {

    @Schema(description = "动作名称", example = "获取用户信息")
    @Size(max = 128, message = "动作名称不能超过128个字符")
    private String actionName;

    @Schema(description = "动作描述", example = "通过HTTP GET获取用户信息")
    @Size(max = 512, message = "动作描述不能超过512个字符")
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

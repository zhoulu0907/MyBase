package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 连接器动作响应VO
 *
 * @author onebase
 * @since 2026-01-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "连接器动作响应")
public class ConnectorActionVO {

    @Schema(description = "动作名称", example = "获取用户信息")
    private String actionName;

    @Schema(description = "动作描述", example = "通过HTTP GET获取用户信息")
    private String description;

    @Schema(description = "状态：1-已发布，2-已下架", example = "1")
    private String status;

    @Schema(description = "基础信息配置")
    private Object basicInfo;

    @Schema(description = "入参配置")
    private Object inputConfig;

    @Schema(description = "出参配置")
    private Object outputConfig;

    @Schema(description = "调试配置")
    private Object debugConfig;
}

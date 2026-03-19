package com.cmsr.onebase.module.flow.build.vo;

import com.fasterxml.jackson.databind.JsonNode;
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

    @Schema(description = "创建时间", example = "2026-02-05 16:30:56")
    private String createTime;

    @Schema(description = "更新时间", example = "2026-02-05 18:31:26")
    private String updateTime;

    /**
     * OpenAPI 格式的完整动作配置
     * <p>
     * 包含 path、method、parameters、requestBody、x-onebase、debug 等 OpenAPI 标准字段，
     * 供前端使用统一的 OpenAPI 格式进行展示和编辑。
     */
    @Schema(description = "OpenAPI格式的完整动作配置")
    private JsonNode actionConfig;

    // ========== 以下字段已废弃，保留用于向后兼容 ==========

    @Deprecated
    @Schema(description = "基础信息配置（已废弃，使用 actionConfig）", hidden = true)
    private Object basicInfo;

    @Deprecated
    @Schema(description = "入参配置（已废弃，使用 actionConfig）", hidden = true)
    private Object inputConfig;

    @Deprecated
    @Schema(description = "出参配置（已废弃，使用 actionConfig）", hidden = true)
    private Object outputConfig;

    @Deprecated
    @Schema(description = "调试配置（已废弃，使用 actionConfig.debug）", hidden = true)
    private Object debugConfig;
}
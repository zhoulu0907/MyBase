package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 创建动作请求VO
 *
 * @author onebase
 * @since 2026-03-19
 */
@Schema(description = "创建动作请求VO")
@Data
public class CreateActionReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 所属连接器UUID
     */
    @Schema(description = "所属连接器UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "连接器UUID不能为空")
    private String connectorUuid;

    /**
     * 连接器类型
     */
    @Schema(description = "连接器类型(HTTP/SCRIPT/...)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "连接器类型不能为空")
    private String connectorType;

    /**
     * 动作编码
     */
    @Schema(description = "动作编码")
    private String actionCode;

    /**
     * 动作名称
     */
    @Schema(description = "动作名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "动作名称不能为空")
    private String actionName;

    /**
     * 动作描述
     */
    @Schema(description = "动作描述")
    private String description;

    /**
     * 输入参数Schema(JSON格式)
     */
    @Schema(description = "输入参数Schema(JSON)")
    private String inputSchema;

    /**
     * 输出参数Schema(JSON格式)
     */
    @Schema(description = "输出参数Schema(JSON)")
    private String outputSchema;

    /**
     * 扩展配置(JSON格式)
     * <p>
     * 混合模式：标准化字段(timeout/retryCount/mockResponse) + 类型特有配置
     */
    @Schema(description = "扩展配置(JSON)")
    private String actionConfig;

    /**
     * 启用状态(0-禁用,1-启用)
     * <p>
     * 默认为1（启用）
     */
    @Schema(description = "启用状态(0-禁用,1-启用)，默认为1（启用）", defaultValue = "1")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;
}
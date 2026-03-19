package com.cmsr.onebase.module.flow.build.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新动作请求VO
 *
 * @author onebase
 * @since 2026-03-19
 */
@Schema(description = "更新动作请求VO")
@Data
public class UpdateActionReqVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 动作ID
     */
    @Schema(description = "动作ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "动作ID不能为空")
    private Long id;

    /**
     * 动作编码
     */
    @Schema(description = "动作编码")
    private String actionCode;

    /**
     * 动作名称
     */
    @Schema(description = "动作名称")
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
     */
    @Schema(description = "扩展配置(JSON)")
    private String actionConfig;

    /**
     * 启用状态(0-禁用,1-启用)
     */
    @Schema(description = "启用状态(0-禁用,1-启用)")
    private Integer activeStatus;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sortOrder;
}
package com.cmsr.onebase.module.bpm.core.vo.design.edge.base;

import com.cmsr.onebase.module.bpm.core.dto.edge.EdgeExtDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 边信息
 *
 * @author liyang
 * @date 2025-10-25
 */
@Schema(description = "边信息")
@Data
public class BaseEdgeVO {
    /**
     * 源节点编码
     */
    @Schema(description = "当前流程节点的编码")
    @NotBlank(message = "源节点编码不能为空")
    @JsonProperty("sourceNodeID")
    private String sourceNodeId;

    /**
     * 目标节点编码
     */
    @NotBlank(message = "目标节点编码不能为空")
    @Schema(description = "下一个流程节点的编码")
    @JsonProperty("targetNodeID")
    private String targetNodeId;

    @Schema(description = "边名称")
    private String name;

    /**
     * 边扩展信息
     *
     * 普通边不用传该参数
     *
     */
    private EdgeExtDTO data;

    /**
     * 边状态，运行实例使用
     */
    @Schema(description = "线状态")
    private String runStatus;
}



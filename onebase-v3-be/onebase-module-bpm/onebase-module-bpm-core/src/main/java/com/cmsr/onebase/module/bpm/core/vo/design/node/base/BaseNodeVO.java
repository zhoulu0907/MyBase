package com.cmsr.onebase.module.bpm.core.vo.design.node.base;

import com.cmsr.onebase.module.bpm.core.vo.design.node.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 基础节点配置视图
 *
 * @author liyang
 * @data 2025-10-21
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true,
        defaultImpl = BaseNodeVO.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ApproverNodeVO.class, name = "approver"),
        @JsonSubTypes.Type(value = StartNodeVO.class, name = "start"),
        @JsonSubTypes.Type(value = EndNodeVO.class, name = "end"),
        @JsonSubTypes.Type(value = InitiationNodeVO.class, name = "initiation"),
        @JsonSubTypes.Type(value = CcNodeVO.class, name = "cc"),
        @JsonSubTypes.Type(value = CondNodeVO.class, name = "condition")
        // 需要添加更多类型
})
@Data
@Schema(description = "节点信息视图")
public class BaseNodeVO {
    @NotBlank(message = "节点类型不能为空")
    @Schema(description = "节点类型")
    private String type;

    /**
     * 实际使用的是流程节点编码
     */
    @NotBlank(message = "流程ID不能为空")
    @Schema(description = "流程节点编码")
    private String id;

    /**
     * 节点名称
     */
    @NotBlank(message = "节点名称不能为空")
    @Schema(description = "节点名称")
    private String name;

    /**
     * 前端展示用，透传
     */
    @Schema(description = "元数据")
    private Object meta;
}

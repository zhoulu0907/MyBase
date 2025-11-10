package com.cmsr.onebase.module.bpm.core.dto.node.base;

import com.cmsr.onebase.module.bpm.core.dto.node.ApproverNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.EndNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.InitiationNodeExtDTO;
import com.cmsr.onebase.module.bpm.core.dto.node.StartNodeExtDTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 * 流程节点表里的扩展字段信息
 *
 * @author liyang
 * @data 2025-10-21
 */
@Data
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "nodeType",
    visible = true,
    defaultImpl = BaseNodeExtDTO.class
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ApproverNodeExtDTO.class, name = "approver"),
    @JsonSubTypes.Type(value = StartNodeExtDTO .class, name = "start"),
    @JsonSubTypes.Type(value = EndNodeExtDTO.class, name = "end"),
    @JsonSubTypes.Type(value = InitiationNodeExtDTO.class, name = "initiation")
})
public class BaseNodeExtDTO {
    /**
     * 节点类型
     */
    private String nodeType;

    /**
     * 节点元数据
     */
    private Object meta;
}

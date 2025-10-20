package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import java.util.List;

import lombok.Data;

@Data
public class EntityFieldDataReqDTO {
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    private List<List<ConditionDTO>> conditionDTO;
    
    /**
     * 拼接的AND条件列表，与每个 conditionDTO 条件组都是 AND 关系
     * 会被添加到每个 conditionDTO 条件组中，非必填
     * 例如：(andCondition1 AND andCondition2 AND group1) OR (andCondition1 AND andCondition2 AND group2)
     */
    private List<ConditionDTO> andConditionDTO;

    /**
     * 排序信息
     */
    private List<OrderDto> orderDtos;
    
    /**
     * 查询数量 用于拼接 limit
     */
    private Integer num;

}

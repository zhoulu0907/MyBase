package com.cmsr.onebase.module.metadata.api.datamethod.dto;

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
    private ConditionDTO conditionDTO;
    /**
     * 排序信息
     */
    private OrderDto orderDto;
    /**
     * 查询数量 用于拼接 limit
     */
    private Integer num;

}

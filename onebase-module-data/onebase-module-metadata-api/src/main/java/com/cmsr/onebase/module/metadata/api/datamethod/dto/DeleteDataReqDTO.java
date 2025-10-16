package com.cmsr.onebase.module.metadata.api.datamethod.dto;

import java.util.List;

import lombok.Data;

@Data
public class DeleteDataReqDTO {
    /**
     * 实体ID
     */
    private Long entityId;
    /**
     * 规则定义二维数组，外层数组元素间为OR关系，内层数组元素间为AND关系
     */
    private List<List<ConditionDTO>> conditionDTO;

}

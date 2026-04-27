package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 多表查询计划
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryPlanDTO {
    private PrimaryTableDTO primary;
    private List<JoinTableDTO> joins;
}

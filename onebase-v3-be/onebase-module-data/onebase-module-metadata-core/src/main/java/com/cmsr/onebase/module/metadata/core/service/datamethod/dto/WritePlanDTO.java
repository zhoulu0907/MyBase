package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 多表写入计划
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WritePlanDTO {
    private WritePrimaryDTO primary;
    private List<WriteChildDTO> children;
}

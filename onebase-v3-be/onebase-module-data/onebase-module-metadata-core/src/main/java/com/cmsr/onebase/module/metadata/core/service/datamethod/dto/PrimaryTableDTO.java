package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 主表配置
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrimaryTableDTO {
    private String datasource;
    private String table;
    private String alias;
    private String pk;
}

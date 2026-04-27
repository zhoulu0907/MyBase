package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 写入主表配置
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WritePrimaryDTO {
    private String datasource;
    private String table;
    private String alias;
    private String pk;
    private Boolean softDelete;
    private String deletedColumn;
}

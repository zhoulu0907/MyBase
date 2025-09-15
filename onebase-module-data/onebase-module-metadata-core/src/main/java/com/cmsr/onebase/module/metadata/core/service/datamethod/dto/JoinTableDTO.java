package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 关联表配置
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JoinTableDTO {
    private String datasource;
    private String table;
    private String alias;
    private String joinType; // left/inner, 当前仅支持left
    private JoinOnDTO on;
    private Boolean many; // 是否一对多

    public boolean isMany() {
        return Boolean.TRUE.equals(many);
    }
}

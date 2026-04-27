package com.cmsr.onebase.module.metadata.core.service.datamethod.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 写入子表配置
 *
 * @author matianyu
 * @date 2025-08-27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WriteChildDTO {
    private String datasource;
    private String table;
    private String alias;
    private Boolean many;
    private String fk;
    private Boolean replaceOnUpdate;
    private Boolean softDelete;
    private String deletedColumn;
    private String pk;
    private String parentFkField; // 引用主表主键的外键字段
    private String dataPath; // 在请求数据中的路径(如user.addresses)
}

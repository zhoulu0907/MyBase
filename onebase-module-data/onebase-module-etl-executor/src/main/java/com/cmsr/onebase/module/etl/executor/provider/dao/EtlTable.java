package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/8 12:58
 */
@Data
public class EtlTable {

    private Long datasourceId;

    private String tableName;

    private String metaInfo;

}

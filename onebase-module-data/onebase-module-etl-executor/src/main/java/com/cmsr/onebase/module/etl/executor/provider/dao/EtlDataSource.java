package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/11/9 8:49
 */
@Data
public class EtlDataSource {

    private String datasourceType;

    private String config;
}

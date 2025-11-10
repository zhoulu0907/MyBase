package com.cmsr.onebase.module.etl.common.entity;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/8 13:12
 */
@Data
public class EtlTableColumn {

    private List<EtlColumn> columns;

}

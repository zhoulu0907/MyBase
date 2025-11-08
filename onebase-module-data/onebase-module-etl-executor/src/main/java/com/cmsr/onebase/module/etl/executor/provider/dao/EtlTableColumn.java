package com.cmsr.onebase.module.etl.executor.provider.dao;

import lombok.Data;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/11/8 13:12
 */
@Data
public class EtlTableColumn {

    private List<Column> columns;

    @Data
    public static class Column {

        private String id;

        private String name;

        private String flinkType;

        private Integer ignoreLength;

        private Integer length;

        private Integer ignorePrecision;

        private Integer precision;

        private Integer ignoreScale;

        private Integer scale;

    }

}

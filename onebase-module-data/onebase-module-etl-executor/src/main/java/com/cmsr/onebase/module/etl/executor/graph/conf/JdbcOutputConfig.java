package com.cmsr.onebase.module.etl.executor.graph.conf;

import lombok.Data;

import java.util.List;

@Data
public class JdbcOutputConfig {

    private Long targetTableId;
    private List<OutputField> fields;

    private JdbcConfig jdbcConfig;
}

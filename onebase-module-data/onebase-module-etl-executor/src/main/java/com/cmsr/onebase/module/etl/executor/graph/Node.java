package com.cmsr.onebase.module.etl.executor.graph;

import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.executor.graph.conf.JdbcOutputConfig;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
public class Node {

    private String id;

    private String type;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type",
            visible = true,
            defaultImpl = NodeConfig.class
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = JdbcInputConfig.class, name = "jdbc_input"),
            @JsonSubTypes.Type(value = JdbcOutputConfig.class, name = "jdbc_output"),
    })
    private NodeConfig config;

    private NodeOutput output;

}

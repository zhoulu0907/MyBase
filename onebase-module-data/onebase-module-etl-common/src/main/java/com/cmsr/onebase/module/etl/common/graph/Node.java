package com.cmsr.onebase.module.etl.common.graph;

import com.cmsr.onebase.module.etl.common.graph.conf.JdbcInputConfig;
import com.cmsr.onebase.module.etl.common.graph.conf.JdbcOutputConfig;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
public class Node<T extends NodeConfig> {

    protected String id;

    protected String type;

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
    protected T config;

    protected NodeOutput output;

}

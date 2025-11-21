package com.cmsr.onebase.module.etl.common.graph;

import com.cmsr.onebase.module.etl.common.graph.conf.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.Objects;

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
            @JsonSubTypes.Type(value = PairJoinConfig.class, name = "pair_join"),
            @JsonSubTypes.Type(value = UnionConfig.class, name = "union"),
            @JsonSubTypes.Type(value = SqlConfig.class, name = "sql")
    })
    protected T config;

    protected NodeOutput output;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(id, node.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

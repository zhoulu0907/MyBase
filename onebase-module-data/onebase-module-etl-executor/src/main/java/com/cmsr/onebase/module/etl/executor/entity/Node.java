package com.cmsr.onebase.module.etl.executor.entity;

import com.cmsr.onebase.module.etl.executor.entity.node.InputNode;
import com.cmsr.onebase.module.etl.executor.entity.node.JoinNode;
import com.cmsr.onebase.module.etl.executor.entity.node.OutputNode;
import com.cmsr.onebase.module.etl.executor.entity.node.UnionNode;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.List;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "input", value = InputNode.class),
        @JsonSubTypes.Type(name = "output", value = OutputNode.class),
        @JsonSubTypes.Type(name = "join", value = JoinNode.class),
        @JsonSubTypes.Type(name = "union", value = UnionNode.class)
})
public class Node {

    private String id;

    private String nodeName;

    private String type;

    private Output output;

    @Data
    public static class Output {

        private Boolean verified;

        private List<FieldDefine> fields;

    }

}

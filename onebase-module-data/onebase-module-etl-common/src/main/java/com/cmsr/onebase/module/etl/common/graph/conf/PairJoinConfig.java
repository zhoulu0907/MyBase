package com.cmsr.onebase.module.etl.common.graph.conf;

import com.cmsr.onebase.module.etl.common.graph.NodeConfig;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class PairJoinConfig extends NodeConfig {

    private String leftNodeId;

    private String rightNodeId;

    private String joinType;

    private List<FieldPair> fieldPairs;

    @Data
    public static class FieldPair {

        private String leftFieldFqn;

        private String rightFieldFqn;

    }
}

package com.cmsr.onebase.module.etl.executor.graph;

public enum NodeTypeEnum {
    JDBC_INPUT,
    JDBC_OUTPUT,
    UNION,
    JOIN;

    public String getValue() {
        return this.name().toLowerCase();
    }

    public static NodeTypeEnum of(String nodeType) {
        for (NodeTypeEnum typeEnum : values()) {
            if (typeEnum.name().equalsIgnoreCase(nodeType)) return typeEnum;
        }
        throw new IllegalArgumentException("illegal node type: " + nodeType);
    }
}

package com.cmsr.onebase.module.flow.core.graph;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:01
 */
@Data
public class JsonGraphNode {

    private String id;

    private String type;

    private Map<String, Object> data;

    private List<JsonGraphNode> blocks;

}

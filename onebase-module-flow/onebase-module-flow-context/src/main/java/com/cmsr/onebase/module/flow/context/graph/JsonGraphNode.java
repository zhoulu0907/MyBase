package com.cmsr.onebase.module.flow.context.graph;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/1 11:01
 */
@Data
@JsonDeserialize(using = JsonGraphMapper.JsonGraphNodeDeserializer.class)
public class JsonGraphNode implements Serializable {

    private String id;

    private String type;

    private NodeData data;

    private List<JsonGraphNode> blocks;

}

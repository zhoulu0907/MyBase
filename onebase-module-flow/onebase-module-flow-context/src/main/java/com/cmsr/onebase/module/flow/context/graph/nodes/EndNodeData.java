package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
public class EndNodeData extends NodeData implements Serializable {

    private String prompt;

    private String statusCode;

}

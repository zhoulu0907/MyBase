package com.cmsr.onebase.module.flow.component.sys;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:40
 */
@Data
@NodeType("end")
public class EndNodeData extends NodeData implements Serializable {

    private String prompt;

    private String statusCode;

}

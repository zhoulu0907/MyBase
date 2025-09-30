package com.cmsr.onebase.module.flow.context.graph;

import lombok.Data;

/**
 * @Author：huangjie
 * @Date：2025/9/29 12:02
 */
@Data
public class NodeData {

    private Boolean isInLoop;

    private InLoopDepth inLoopDepth;

}

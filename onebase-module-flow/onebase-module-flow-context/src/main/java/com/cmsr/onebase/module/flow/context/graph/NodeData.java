package com.cmsr.onebase.module.flow.context.graph;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/29 12:02
 */
@ToString
public class NodeData implements Serializable {

    @Getter
    @Setter
    private Boolean inLoop;

    @Setter
    private InLoopDepth inLoopDepth;

    public InLoopDepth getInLoopDepth() {
        return inLoopDepth == null ? InLoopDepth.EMPTY_LOOP_DEPTH : inLoopDepth;
    }

}

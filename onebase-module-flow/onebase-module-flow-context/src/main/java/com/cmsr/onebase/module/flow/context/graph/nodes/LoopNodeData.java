package com.cmsr.onebase.module.flow.context.graph.nodes;

import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.context.graph.NodeType;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author：huangjie
 * @Date：2025/9/30 9:16
 */
@Data
@NodeType("loop")
public class LoopNodeData extends NodeData implements Serializable {

    /**
     * break  直接阻断，跳出循环
     * continue 继续执行下一次循环
     */
    private String breakMode;

    private String dataNodeId;

    public boolean isBreakMode() {
        return "break".equalsIgnoreCase(this.breakMode);
    }

    public boolean isContinueMode() {
        return "continue".equalsIgnoreCase(this.breakMode);
    }

}

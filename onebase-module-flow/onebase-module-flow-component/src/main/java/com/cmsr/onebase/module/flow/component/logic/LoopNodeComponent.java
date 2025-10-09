package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.LoopNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeForComponent;
import lombok.Setter;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/28 14:46
 */
@Setter
@LiteflowComponent("loop")
public class LoopNodeComponent extends NodeForComponent {

    @Override
    public int processFor() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        LoopNodeData nodeData = (LoopNodeData) executeContext.getNodeData(this.getTag());
        //
        String dataNodeId = nodeData.getDataNodeId();
        Object value = variableContext.getVariableByExpression(dataNodeId);
        if (value == null) {
            return 0;
        }
        if (value instanceof List l) {
            variableContext.putNodeVariables(this.getTag(), l);
            return l.size();
        }
        throw new IllegalStateException("变量" + dataNodeId + "不是List类型: " + value.getClass().getName());
    }

}

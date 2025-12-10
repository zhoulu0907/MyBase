package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.LoopNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeForComponent;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/28 14:46
 */
@Setter
@LiteflowComponent("loop")
public class LoopNodeComponent extends NodeForComponent {

    @Override
    public int processFor() {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("循环节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        LoopNodeData nodeData = (LoopNodeData) executeContext.getNodeData(this.getTag());
        //
        String dataNodeId = nodeData.getDataNodeId();
        List<Map<String, Object>> value = variableContext.getListVariableByTag(dataNodeId);
        if (value == null) {
            executeContext.addLog("循环节点数据源为空");
            return 0;
        }
        executeContext.addLog("循环节点循环数量：" + value.size());
        variableContext.putNodeVariables(this.getTag(), value);
        return value.size();
    }

}

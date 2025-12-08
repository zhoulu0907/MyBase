package com.cmsr.onebase.module.flow.component.interact;

import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.context.provider.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.NavigateNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("navigate")
public class NavigateNodeComponent extends SkippableNodeComponent {

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("跳转节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        NavigateNodeData nodeData = (NavigateNodeData) executeContext.getNodeData(this.getTag());
        variableContext.setOutputParams(nodeData.toMap());
    }

}

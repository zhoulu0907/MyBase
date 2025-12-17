package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.logic.SwitchConditionNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("switchCondition")
public class SwitchConditionNodeComponent extends NodeSwitchComponent {

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public String processSwitch() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("分支节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        SwitchConditionNodeData nodeData = (SwitchConditionNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        //
        if (executeContext.hasNodeProcessResult(this.getTag())) {
            String result = (String) executeContext.getNodeProcessResult(this.getTag());
            executeContext.addLog("分支节点已执行过，直接返回结果: " + result);
            return result;
        }
        //
        String result = "tag:" + evaluateSwitchCondition(nodeData, expressionContext);
        executeContext.addLog("分支节点执行完毕，结果为: " + result);
        //
        executeContext.putNodeProcessResult(this.getTag(), result);
        return result;
    }

    private String evaluateSwitchCondition(SwitchConditionNodeData nodeData, Map<String, Object> expressionContext) {
        //
        for (SwitchConditionNodeData.Case aCase : nodeData.getCases()) {
            List<Conditions> conditions = aCase.getFilterCondition();
            OrExpression orExpression = flowConditionsProvider.formatConditionsForExpression(conditions, expressionContext);
            boolean evaluated = expressionExecutor.evaluateContext(orExpression, expressionContext);
            if (evaluated) {
                return aCase.getId();
            }
        }
        return nodeData.getDefaultId();
    }

}

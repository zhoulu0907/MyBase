package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.SwitchConditionNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeSwitchComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("switchCondition")
public class SwitchConditionNodeComponent extends NodeSwitchComponent {

    @Autowired
    private ConditionsProvider conditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public String processSwitch() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        SwitchConditionNodeData nodeData = (SwitchConditionNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        //
        if (executeContext.hasNodeProcessResult(this.getTag())) {
            return (String) executeContext.getNodeProcessResult(this.getTag());
        }
        //
        String result = "tag:" + evaluateSwitchCondition(nodeData, variableContext, inLoopDepth);
        //
        executeContext.putNodeProcessResult(this.getTag(), result);
        return result;
    }

    private String evaluateSwitchCondition(SwitchConditionNodeData nodeData, VariableContext variableContext, InLoopDepth inLoopDepth) {
        for (SwitchConditionNodeData.Case aCase : nodeData.getCases()) {
            List<Conditions> conditions = aCase.getFilterCondition();
            OrExpression orExpression = conditionsProvider.formatConditionsForExpression(this, variableContext, inLoopDepth, conditions);
            boolean evaluated = expressionExecutor.evaluate(orExpression, variableContext.getNodeVariables());
            if (evaluated) {
                return aCase.getId();
            }
        }
        return nodeData.getDefaultId();
    }

}

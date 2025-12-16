package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.logic.IfCaseNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("ifCase")
public class IfCaseNodeComponent extends NodeBooleanComponent {

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public boolean processBoolean() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("条件节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        IfCaseNodeData nodeData = (IfCaseNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());
        //
        if (executeContext.hasNodeProcessResult(this.getTag())) {
            Boolean result = (Boolean) executeContext.getNodeProcessResult(this.getTag());
            executeContext.addLog("条件节点已执行过，直接返回结果: " + result);
        }
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = flowConditionsProvider.formatConditionsForExpression(conditions, expressionContext);
        boolean evaluated = expressionExecutor.evaluate(orExpression, expressionContext);
        //
        executeContext.putNodeProcessResult(this.getTag(), evaluated);
        executeContext.addLog("条件节点执行完毕，结果为: " + evaluated);
        return evaluated;
    }


}

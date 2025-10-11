package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.IfCaseNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("ifCase")
public class IfCaseNodeComponent extends NodeBooleanComponent {

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Autowired
    private ExpressionExecutor expressionExecutor;

    @Override
    public boolean processBoolean() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        IfCaseNodeData nodeData = (IfCaseNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = conditionsProvider.formatConditionsForExpression(this, variableContext, inLoopDepth, conditions);
        boolean evaluated = expressionExecutor.evaluate(orExpression, variableContext.getNodeVariables());
        //
        return evaluated;
    }


}

package com.cmsr.onebase.module.flow.component.external;

import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataCalcNodeData;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataCalc")
public class DataCalcNodeComponent extends SkippableNodeComponent {


    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    @Override
    public void process() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据计算节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataCalcNodeData nodeData = (DataCalcNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        List<ConditionItem> conditionItems = nodeData.getCalRules();
        // 固定是字符串类型
        for (ConditionItem conditionItem : conditionItems) {
            conditionItem.setFieldTypeEnum(SemanticFieldTypeEnum.TEXT);
            //conditionItem.setOp(OpEnum.EQUALS.name());
        }
        List<ExpressionItem> expressionItems = flowConditionsProvider.formatConditionItemsForValue(conditionItems, expressionContext);
        Map<String, Object> dataMap = expressionItems.stream().collect(Collectors.toMap(ExpressionItem::getFieldKey, ExpressionItem::getFieldValue));
        executeContext.addLog("数据计算节点执行返回数据：" + dataMap.size());
        variableContext.putNodeVariables(this.getTag(), dataMap);
    }


}

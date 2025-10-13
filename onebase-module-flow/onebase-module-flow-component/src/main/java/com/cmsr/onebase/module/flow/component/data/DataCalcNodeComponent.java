package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.express.JdbcTypeEnum;
import com.cmsr.onebase.framework.common.express.OpEnum;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataCalcNodeData;
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
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        log.info("DataCalcNodeComponent process - 开始处理节点数据添加操作");
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataCalcNodeData nodeData = (DataCalcNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        List<ConditionItem> conditionItems = nodeData.getCalRules();
        // 固定是字符串类型
        for (ConditionItem conditionItem : conditionItems) {
            conditionItem.setJdbcType(JdbcTypeEnum.VARCHAR.getCode());
            conditionItem.setOp(OpEnum.EQUALS.name());
        }
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(this, variableContext, inLoopDepth, conditionItems);
        Map<String, Object> dataMap = expressionItems.stream().collect(Collectors.toMap(ExpressionItem::getKey, ExpressionItem::getValue));
        variableContext.putNodeVariables(this.getTag(), dataMap);
    }


}

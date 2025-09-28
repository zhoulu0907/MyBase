package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.component.utils.ValueProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.express.AndExpresses;
import com.cmsr.onebase.module.flow.context.express.ExpressItem;
import com.cmsr.onebase.module.flow.context.express.ExpressionProvider;
import com.cmsr.onebase.module.flow.context.express.OrExpresses;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import lombok.Setter;
import org.apache.commons.jexl3.JexlExpression;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    private ValueProvider valueProvider;

    @Autowired
    private ExpressionProvider expressionProvider;

    @Override
    public boolean processBoolean() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        //
        List<Map<String, Object>> filterCondition = (List<Map<String, Object>>) nodeData.get("filterCondition");
        List<ConditionItem> conditions = Condition.createCondition(filterCondition);
        OrExpresses orExpresses = convertToOrExpresses(conditions, variableContext);
        JexlExpression compiledExpression = expressionProvider.compileExpression(orExpresses);
        boolean evaluated = expressionProvider.evaluate(compiledExpression, variableContext.getNodeVariables());
        //
        return evaluated;
    }

    private OrExpresses convertToOrExpresses(List<ConditionItem> conditions, VariableContext variableContext) {
        List<AndExpresses> andExpressesList = new ArrayList<>();
        for (ConditionItem condition : conditions) {
            AndExpresses andExpresses = convertToAndExpresses(condition, variableContext);
            andExpressesList.add(andExpresses);
        }
        OrExpresses orExpresses = new OrExpresses();
        orExpresses.setExpressesList(andExpressesList);
        return orExpresses;
    }

    private AndExpresses convertToAndExpresses(ConditionItem condition, VariableContext variableContext) {
        List<ExpressItem> expressItemList = new ArrayList<>();
        for (RuleItem ruleItem : condition.getRules()) {
            Object value = valueProvider.convertValue(ruleItem.getOperatorType(), ruleItem.getValue(), variableContext);
            ExpressItem expressItem = new ExpressItem();
            expressItem.setKey(ruleItem.getFieldId());
            expressItem.setOp(ruleItem.getOp());
            expressItem.setValue(value);
            expressItemList.add(expressItem);
        }
        AndExpresses andExpresses = new AndExpresses();
        andExpresses.setExpressItems(expressItemList);
        return andExpresses;
    }
}

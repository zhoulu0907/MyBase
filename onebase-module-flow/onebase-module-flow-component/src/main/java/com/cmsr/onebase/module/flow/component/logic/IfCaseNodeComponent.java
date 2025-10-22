package com.cmsr.onebase.module.flow.component.logic;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@LiteflowComponent("ifCase")
public class IfCaseNodeComponent extends NodeBooleanComponent {

    @Override
    public boolean processBoolean() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        Map<String, Object> nodeData = executeContext.getNodeData(this.getTag());
        //
        List<Map<String, Object>> filterCondition = (List<Map<String, Object>>) nodeData.get("filterCondition");
        List<ConditionItem> condition = Condition.createCondition(filterCondition);
        //
        return false;
    }

}

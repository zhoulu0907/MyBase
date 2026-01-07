package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticTargetConditionVO;
import com.mybatisflex.core.tenant.TenantManager;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataUpdate")
public class DataUpdateNodeComponent extends SkippableNodeComponent {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据更新节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataUpdateNodeData nodeData = (DataUpdateNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());
        //
        SemanticTargetConditionVO reqDTO = new SemanticTargetConditionVO();
        reqDTO.setTraceId(executeContext.getTraceId());
        reqDTO.setTableName(nodeData.resolveTargetTableName());
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = flowConditionsProvider.formatConditionsForValue(conditions, expressionContext);
        reqDTO.setSemanticConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        //
        List<ConditionItem> fields = nodeData.getFields();
        reqDTO.setUpdateProperties(buildSingleReqData(fields, expressionContext, executeContext));
        //
        List<SemanticEntityValueDTO> respDTOSS = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                executeContext.getApplicationId(),
                executeContext.getVersionTag(),
                () -> semanticDynamicDataApi.updateDataByCondition(reqDTO)
        ));
        executeContext.addLog("数据更新节点更新数据量: " + respDTOSS.size());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(respDTOSS));
    }

    private Map<String, Object> buildSingleReqData(List<ConditionItem> conditionItems, Map<String, Object> vars, ExecuteContext executeContext) {
        List<ExpressionItem> expressionItems = flowConditionsProvider.formatConditionItemsForValue(conditionItems, vars);
        Map<String, Object> data = new HashMap<>();
        for (ExpressionItem expressionItem : expressionItems) {
            data.put(DataMethodApiHelper.convertToFieldName(expressionItem.getFieldKey()), expressionItem.getFieldValue());
        }
        Map<String, String> systemFields = DataMethodApiHelper.extractSystemFields(executeContext);
        data.putAll(systemFields);
        return data;
    }

}

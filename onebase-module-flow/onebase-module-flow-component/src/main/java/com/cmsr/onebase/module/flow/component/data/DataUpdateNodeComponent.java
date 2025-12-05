package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.ExpressionItem;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataUpdateNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicTargetConditionVO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据更新节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataUpdateNodeData nodeData = (DataUpdateNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        //
        SemanicTargetConditionVO reqDTO = new SemanicTargetConditionVO();
        reqDTO.setTraceId(executeContext.getTraceId());
        if (StringUtils.equalsIgnoreCase("mainEntity", nodeData.getUpdateType())) {
            reqDTO.setTableName(nodeData.getMainEntityName());
        } else if (StringUtils.equalsIgnoreCase("subEntity", nodeData.getUpdateType())) {
            reqDTO.setTableName(nodeData.getSubEntityName());
        } else {
            throw new IllegalArgumentException("updateType 类型错误: " + nodeData.getUpdateType());
        }
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
        reqDTO.setSemanticConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        //
        List<ConditionItem> fields = nodeData.getFields();
        reqDTO.setUpdateProperties(buildSingleReqData(fields, expressionContext));
        //
        List<SemanticEntityValueDTO> respDTOSS = ApplicationManager.withApplicationId(executeContext.getApplicationId(), () -> semanticDynamicDataApi.updateDataByCondition(reqDTO));
        executeContext.addLog("数据更新节点更新数据量: " + respDTOSS.size());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(respDTOSS));
    }

    private Map<String, Object> buildSingleReqData(List<ConditionItem> conditionItems, Map<String, Object> vars) {
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(conditionItems, vars);
        Map<String, Object> data = new HashMap<>();
        for (ExpressionItem expressionItem : expressionItems) {
            data.put(expressionItem.getKey(), expressionItem.getValue());
        }
        return data;
    }

}

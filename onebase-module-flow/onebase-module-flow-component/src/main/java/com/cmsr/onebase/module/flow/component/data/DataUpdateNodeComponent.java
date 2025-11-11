package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
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
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    private DataMethodApi dataMethodApi;

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
        UpdateDataReqDTO reqDTO = new UpdateDataReqDTO();
        reqDTO.setTraceId(executeContext.getTraceId());
        if (StringUtils.equalsIgnoreCase("mainEntity", nodeData.getUpdateType())) {
            reqDTO.setEntityId(nodeData.getMainEntityId());
        } else if (StringUtils.equalsIgnoreCase("subEntity", nodeData.getUpdateType())) {
            reqDTO.setEntityId(nodeData.getSubEntityId());
        } else {
            throw new IllegalArgumentException("updateType 类型错误: " + nodeData.getUpdateType());
        }
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
        reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        //
        List<ConditionItem> fields = nodeData.getFields();
        reqDTO.setData(buildSingleReqData(fields, expressionContext));
        //
        List<List<EntityFieldDataRespDTO>> respDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.updateData(reqDTO));
        executeContext.addLog("数据更新节点更新数据量: " + respDTOSS.size());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(respDTOSS));
    }

    private List<Map<Long, Object>> buildSingleReqData(List<ConditionItem> conditionItems, Map<String, Object> vars) {
        List<Map<Long, Object>> reqData = new ArrayList<>();
        List<ExpressionItem> expressionItems = conditionsProvider.formatConditionItemsForValue(conditionItems, vars);
        Map<Long, Object> data = new HashMap<>();
        for (ExpressionItem expressionItem : expressionItems) {
            data.put(NumberUtils.toLong(expressionItem.getKey()), expressionItem.getValue());
        }
        reqData.add(data);
        return reqData;
    }

}

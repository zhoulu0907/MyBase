package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.condition.RuleItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.UpdateDataReqDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
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
public class DataUpdateNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;

    @Override
    public void process() throws Exception {
        log.info("DataUpdateNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        NodeData nodeData = executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        //
        List<Map<String, Object>> filterCondition = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "filterCondition");
        List<ConditionItem> conditionItems = Condition.createCondition(filterCondition);
        conditionItems = conditionsProvider.formatForExpression(this, conditionItems, inLoopDepth);
        conditionItems = conditionsProvider.formatForValue(conditionItems, variableContext);
        //
        List<Map<String, Object>> fields = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "fields");
        List<RuleItem> ruleItems = Condition.createRuleItems(fields);

        UpdateDataReqDTO   reqDTO = new UpdateDataReqDTO();
        reqDTO.setEntityId(nodeData.getLong("mainEntityId"));
        if (reqDTO.getEntityId() == null) {
            reqDTO.setEntityId(nodeData.getLong("subEntityId"));
        }
        reqDTO.setConditionDTO(dataMethodApiHelper.processFilterCondition(conditionItems));
        reqDTO.setData(buildSingleReqData(ruleItems, inLoopDepth, variableContext));
        List<List<EntityFieldDataRespDTO>> respDTOSS = TenantUtils.executeIgnore(() -> dataMethodApi.updateData(reqDTO));
        variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToListMap(respDTOSS));
    }

    private List<Map<Long, Object>> buildSingleReqData(List<RuleItem> ruleItems, InLoopDepth inLoopDepth, VariableContext variableContext) {
        List<Map<Long, Object>> reqData = new ArrayList<>();
        ruleItems = conditionsProvider.formatRuleItemsForExpression(this, ruleItems, inLoopDepth);
        ruleItems = conditionsProvider.formatRuleItemsForValue(ruleItems, variableContext);
        Map<Long, Object> data = new HashMap<>();
        for (RuleItem ruleItem : ruleItems) {
            data.put(NumberUtils.toLong(ruleItem.getFieldId()), ruleItem.getValue());
        }
        reqData.add(data);
        return reqData;
    }

}

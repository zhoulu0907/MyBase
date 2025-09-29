package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.VariableConstants;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Condition;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataQuery")
public class DataQueryNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private DataMethodApiHelper dataMethodApiHelper;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        log.info("DataQueryNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        NodeData nodeData = executeContext.getNodeData(this.getTag());
        // 转换成数据方法参数
        List<Map<String, Object>> filterCondition = (List<Map<String, Object>>) MapUtils.getObject(nodeData, "filterCondition");
        List<ConditionItem> conditionItems = Condition.createCondition(filterCondition);
        InLoopDepth inLoopDepth =  nodeData.getInLoopDepth();
        conditionItems = conditionsProvider.formatForExpression(this, conditionItems, inLoopDepth);
        conditionItems = conditionsProvider.formatForValue(conditionItems, variableContext);
        // 数据方法参数
        EntityFieldDataReqDTO reqDTO = dataMethodApiHelper.convertQueryReq(nodeData, conditionItems);
        reqDTO.setNum(1);
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOS)) {
            variableContext.putNodeVariables(this.getTag(), dataMethodApiHelper.convertToMap(fieldDataRespDTOS.get(0)));
        }
    }


}

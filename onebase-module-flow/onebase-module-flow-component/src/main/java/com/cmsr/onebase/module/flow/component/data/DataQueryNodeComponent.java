package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataQueryNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        log.info("DataQueryNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataQueryNodeData nodeData = (DataQueryNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        // 转换成数据方法参数
        List<ConditionItem> conditionItems = nodeData.getFilterCondition();
        conditionItems = conditionsProvider.formatForExpression(this, conditionItems, inLoopDepth);
        conditionItems = conditionsProvider.formatForValue(conditionItems, variableContext);
        // 数据方法参数
        EntityFieldDataReqDTO reqDTO = new EntityFieldDataReqDTO();
        if (nodeData.getMainEntityId() != null) {
            reqDTO.setEntityId(nodeData.getMainEntityId());
        } else {
            reqDTO.setEntityId(nodeData.getSubEntityId());
        }
        reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(conditionItems));
        reqDTO.setOrderDtos(DataMethodApiHelper.processSortCondition(nodeData.getSortBy()));

        reqDTO.setNum(1);
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOS)) {
            variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToMap(fieldDataRespDTOS.get(0)));
        }
    }


}

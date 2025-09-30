package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.ConditionItem;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataDeleteeNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@Setter
@LiteflowComponent("dataDelete")
public class DataDeleteNodeComponent extends NormalNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        log.info("DataDeleteNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataDeleteeNodeData nodeData = (DataDeleteeNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        //
        List<ConditionItem> conditionItems = nodeData.getFilterCondition();
        conditionItems = conditionsProvider.formatForExpression(this, conditionItems, inLoopDepth);
        conditionItems = conditionsProvider.formatForValue(conditionItems, variableContext);

        DeleteDataReqDTO reqDTO = new DeleteDataReqDTO();
        reqDTO.setEntityId(nodeData.getMainEntityId());
        if (reqDTO.getEntityId() == null) {
            reqDTO.setEntityId(nodeData.getSubEntityId());
        }
        reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(conditionItems));

        TenantUtils.executeIgnore(() -> dataMethodApi.deleteDataByCondition(reqDTO));
    }

}

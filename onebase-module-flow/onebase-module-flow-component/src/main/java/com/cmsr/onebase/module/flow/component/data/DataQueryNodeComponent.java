package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataQueryNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
public class DataQueryNodeComponent extends SkippableNodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据查询节点（单条）开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataQueryNodeData nodeData = (DataQueryNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        // 转换成数据方法参数
        EntityFieldDataReqDTO reqDTO = new EntityFieldDataReqDTO();
        if (StringUtils.equalsIgnoreCase("mainEntity", nodeData.getDataType())) {
            reqDTO.setEntityId(nodeData.getMainEntityId());
        } else if (StringUtils.equalsIgnoreCase("subEntity", nodeData.getDataType())) {
            reqDTO.setEntityId(nodeData.getSubEntityId());
        }
        if (!StringUtils.equalsIgnoreCase("all", nodeData.getFilterType())) {
            List<Conditions> conditions = nodeData.getFilterCondition();
            OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
            reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        reqDTO.setOrderDtos(DataMethodApiHelper.processSortCondition(nodeData.getSortBy()));
        reqDTO.setNum(1);
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        executeContext.addLog("数据查询节点（单条）返回数据量: " + fieldDataRespDTOS.size());
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOS)) {
            variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToMap(fieldDataRespDTOS.get(0)));
        }
    }


}

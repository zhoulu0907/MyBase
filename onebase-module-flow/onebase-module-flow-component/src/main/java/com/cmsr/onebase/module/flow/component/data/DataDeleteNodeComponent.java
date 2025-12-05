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
import com.cmsr.onebase.module.flow.context.graph.nodes.DataDeleteeNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.DeleteDataReqDTO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanicTargetConditionVO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
@LiteflowComponent("dataDelete")
public class DataDeleteNodeComponent extends SkippableNodeComponent {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("数据删除节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        DataDeleteeNodeData nodeData = (DataDeleteeNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());
        //
        List<Conditions> conditions = nodeData.getFilterCondition();
        OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);

        SemanicTargetConditionVO reqDTO = new SemanicTargetConditionVO();
        reqDTO.setTraceId(executeContext.getTraceId());
        if (StringUtils.equalsIgnoreCase("mainEntity", nodeData.getDataType())) {
            reqDTO.setTableName(nodeData.getMainEntityName());
        } else if (StringUtils.equalsIgnoreCase("subEntity", nodeData.getDataType())) {
            reqDTO.setTableName(nodeData.getSubEntityName());
        } else {
            throw new IllegalArgumentException("dataType 类型错误: " + nodeData.getDataType());
        }
        if (!StringUtils.equalsIgnoreCase("all", nodeData.getFilterType())) {
            reqDTO.setSemanticConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        Integer result = TenantUtils.executeIgnore(() -> semanticDynamicDataApi.deleteDataByCondition(reqDTO));
        executeContext.addLog("数据删除节点, 删除数量: " + result);
    }

}

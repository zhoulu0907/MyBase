package com.cmsr.onebase.module.flow.component.data;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.DataQueryNodeData;
import com.cmsr.onebase.module.flow.context.provider.ConditionsProvider;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.mybatisflex.core.tenant.TenantManager;
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
    private SemanticDynamicDataApi semanticDynamicDataApi;

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
        SemanticPageConditionVO reqDTO = new SemanticPageConditionVO();
        reqDTO.setTableName(nodeData.resolveTargetTableName());
        if (!StringUtils.equalsIgnoreCase("all", nodeData.getFilterType())) {
            List<Conditions> conditions = nodeData.getFilterCondition();
            OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
            reqDTO.setSemanticConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        reqDTO.setSortBy(DataMethodApiHelper.processSortCondition(nodeData.getSortBy()));
        reqDTO.setPageNo(1);
        reqDTO.setPageSize(1);
        PageResult<SemanticEntityValueDTO> fieldDataRespDTOS = TenantManager.withoutTenantCondition(() -> ApplicationManager.withApplicationIdAndVersionTag(
                executeContext.getApplicationId(),
                executeContext.getVersionTag(),
                () -> semanticDynamicDataApi.getDataByCondition(reqDTO)));
        executeContext.addLog("数据查询节点（单条）返回数据量: " + fieldDataRespDTOS.getTotal());
        if (CollectionUtils.isNotEmpty(fieldDataRespDTOS.getList())) {
            Map<String, Object> result = DataMethodApiHelper.convertToMap(fieldDataRespDTOS.getList().get(0));
            variableContext.putNodeVariables(this.getTag(), result);
        }
    }


}

package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.data.DataMethodApiHelper;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.provider.ConditionsProvider;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Setter
@Slf4j
@LiteflowComponent("startDateField")
public class StartDateFieldNodeComponent extends NodeComponent {

    @Autowired
    private SemanticDynamicDataApi semanticDynamicDataApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("时间字段触发开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
        StartDateFieldNodeData nodeData = (StartDateFieldNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        SemanticFieldTypeEnum fieldJdbcType = nodeData.getOffsetFiledTypeEnum();

        SemanticPageConditionVO reqDTO = new SemanticPageConditionVO();
        reqDTO.setTableName(nodeData.getTableName());
        if (fieldJdbcType == SemanticFieldTypeEnum.DATETIME) {
            List<String> values = nodeData.calculateOffTime(LocalDateTime.now());
            SemanticConditionDTO rangeCondition = DataMethodApiHelper.extractFromOperator(OpEnum.RANGE, values);
            reqDTO.setSemanticConditionDTO(rangeCondition);
        } else if (fieldJdbcType == SemanticFieldTypeEnum.DATE) {
            String value = nodeData.calculateOffTime(LocalDate.now());
            SemanticConditionDTO eqCondition = DataMethodApiHelper.extractFromOperator(OpEnum.EQUALS, List.of(value));
            reqDTO.setSemanticConditionDTO(eqCondition);
        }

        if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
            List<Conditions> conditions = nodeData.getFilterCondition();
            OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
            reqDTO.setSemanticConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        if (nodeData.isBatchMode()) {
            reqDTO.setPageNo(1);
            reqDTO.setPageSize(nodeData.getBatchSize());
        }
        executeContext.addLog("时间字段触发开始查询数据");
        PageResult<SemanticEntityValueDTO> fieldDataRespDTOS = ApplicationManager.withApplicationIdAndVersionTag(
                executeContext.getApplicationId(),
                executeContext.getVersionTag(),
                () -> semanticDynamicDataApi.getDataByCondition(reqDTO));
        executeContext.addLog("时间字段触发查询返回数据量: " + fieldDataRespDTOS.getTotal());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(fieldDataRespDTOS.getList()));
    }

}

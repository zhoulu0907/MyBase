package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.module.flow.component.data.DataMethodApiHelper;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticConditionDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticCombinatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticConditionNodeTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticFieldTypeEnum;
import com.cmsr.onebase.module.metadata.core.semantic.dto.enums.SemanticOperatorEnum;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
import com.google.common.collect.Lists;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private FlowConditionsProvider flowConditionsProvider;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("时间字段触发开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
        StartDateFieldNodeData nodeData = (StartDateFieldNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        SemanticFieldTypeEnum filedTypeEnum = nodeData.getOffsetFiledTypeEnum();
        List<SemanticConditionDTO> andSections = new ArrayList<>();
        SemanticConditionDTO timeCondition = createTimeCondition(filedTypeEnum, nodeData);
        andSections.add(timeCondition);
        if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
            List<Conditions> conditions = nodeData.getFilterCondition();
            OrExpression orExpression = flowConditionsProvider.formatConditionsForValue(conditions, expressionContext);
            SemanticConditionDTO filterCondition = DataMethodApiHelper.processFilterCondition(orExpression);
            andSections.add(filterCondition);
        }
        SemanticConditionDTO rootConditions = new SemanticConditionDTO();
        rootConditions.setNodeType(SemanticConditionNodeTypeEnum.GROUP);
        rootConditions.setCombinator(SemanticCombinatorEnum.AND);
        rootConditions.setChildren(andSections);

        SemanticPageConditionVO reqDTO = new SemanticPageConditionVO();
        reqDTO.setTableName(nodeData.getTableName());
        reqDTO.setSemanticConditionDTO(rootConditions);
        reqDTO.setPageNo(1);
        reqDTO.setPageSize(nodeData.getBatchSize());
        executeContext.addLog("时间字段触发开始查询数据");
        PageResult<SemanticEntityValueDTO> fieldDataRespDTOS = ApplicationManager.withApplicationIdAndVersionTag(
                executeContext.getApplicationId(),
                executeContext.getVersionTag(),
                () -> semanticDynamicDataApi.getDataByCondition(reqDTO));
        executeContext.addLog("时间字段触发查询返回数据量: " + fieldDataRespDTOS.getTotal());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(fieldDataRespDTOS.getList()));
    }

    private SemanticConditionDTO createTimeCondition(SemanticFieldTypeEnum filedTypeEnum, StartDateFieldNodeData nodeData) {
        if (filedTypeEnum == SemanticFieldTypeEnum.DATETIME) {
            List<String> values = nodeData.calculateOffTime(LocalDateTime.now());
            SemanticConditionDTO filterCondition = new SemanticConditionDTO();
            filterCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
            filterCondition.setFieldName(nodeData.getOffsetFieldName());
            filterCondition.setFieldValue(Lists.newArrayList(values));
            filterCondition.setOperator(SemanticOperatorEnum.RANGE);
            return filterCondition;
        } else if (filedTypeEnum == SemanticFieldTypeEnum.DATE) {
            String value = nodeData.calculateOffTime(LocalDate.now());
            SemanticConditionDTO filterCondition = new SemanticConditionDTO();
            filterCondition.setNodeType(SemanticConditionNodeTypeEnum.CONDITION);
            filterCondition.setFieldName(nodeData.getOffsetFieldName());
            filterCondition.setFieldValue(Lists.newArrayList(value));
            filterCondition.setOperator(SemanticOperatorEnum.EQUALS);
            return filterCondition;
        } else {
            throw new IllegalArgumentException("参数offsetFiledType错误: " + nodeData.getOffsetFieldName() + ":" + filedTypeEnum);
        }
    }

}

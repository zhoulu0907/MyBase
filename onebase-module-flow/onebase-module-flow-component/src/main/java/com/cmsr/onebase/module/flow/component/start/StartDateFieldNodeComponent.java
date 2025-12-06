package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.common.security.ApplicationManager;
import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.data.DataMethodApiHelper;
import com.cmsr.onebase.module.flow.component.utils.VariableProvider;
import com.cmsr.onebase.module.flow.context.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.cmsr.onebase.module.metadata.api.semantic.SemanticDynamicDataApi;
import com.cmsr.onebase.module.metadata.core.semantic.dto.SemanticEntityValueDTO;
import com.cmsr.onebase.module.metadata.core.semantic.vo.SemanticPageConditionVO;
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
    private ConditionsProvider conditionsProvider;

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("时间字段触发开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
        StartDateFieldNodeData nodeData = (StartDateFieldNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        Map<String, Object> expressionContext = VariableProvider.resolveLoopVariables(this, inLoopDepth, variableContext.getNodeVariables());

        JdbcTypeEnum fieldJdbcType = queryFieldType(nodeData.getOffsetFiledId());

        SemanticPageConditionVO reqDTO = new SemanticPageConditionVO();
        reqDTO.setTableName(nodeData.getEntityName());
        if (fieldJdbcType == JdbcTypeEnum.TIMESTAMP) {
            List<String> values = nodeData.calculateOffTime(LocalDateTime.now());
            List<ConditionDTO> andConditionDTO = new ArrayList<>();
            {
                ConditionDTO conditionDTO = new ConditionDTO();
                conditionDTO.setFieldId(nodeData.getOffsetFiledId());
                conditionDTO.setOperator(OpEnum.GREATER_EQUALS.name());
                conditionDTO.setFieldValue(List.of(values.get(0)));
                andConditionDTO.add(conditionDTO);
            }
            {
                ConditionDTO conditionDTO = new ConditionDTO();
                conditionDTO.setFieldId(nodeData.getOffsetFiledId());
                conditionDTO.setOperator(OpEnum.LESS_EQUALS.name());
                conditionDTO.setFieldValue(List.of(values.get(1)));
                andConditionDTO.add(conditionDTO);
            }
            reqDTO.setAndConditionDTO(andConditionDTO);
        } else if (fieldJdbcType == JdbcTypeEnum.DATE) {
            String value = nodeData.calculateOffTime(LocalDate.now());
            List<ConditionDTO> andConditionDTO = new ArrayList<>();
            ConditionDTO conditionDTO = new ConditionDTO();
            conditionDTO.setFieldId(nodeData.getOffsetFiledId());
            conditionDTO.setOperator(OpEnum.EQUALS.name());
            conditionDTO.setFieldValue(List.of(value));
            andConditionDTO.add(conditionDTO);
            reqDTO.setAndConditionDTO(andConditionDTO);
        }

        if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
            List<Conditions> conditions = nodeData.getFilterCondition();
            OrExpression orExpression = conditionsProvider.formatConditionsForValue(conditions, expressionContext);
            reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        if (nodeData.isBatchMode()) {
            reqDTO.setPageNo(1);
            reqDTO.setPageSize(nodeData.getBatchSize());
        }
        executeContext.addLog("时间字段触发开始查询数据");
        PageResult<SemanticEntityValueDTO> fieldDataRespDTOS = ApplicationManager.withApplicationIdAndVersionTag(nodeData.getApplicationId(), () -> semanticDynamicDataApi.getDataByCondition(reqDTO));
        executeContext.addLog("时间字段触发查询返回数据量: " + fieldDataRespDTOS.getTotal());
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(fieldDataRespDTOS.getList()));
    }

    private JdbcTypeEnum queryFieldType(Long filedId) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(List.of(filedId));
        List<EntityFieldJdbcTypeRespDTO> respDTOS = TenantUtils.executeIgnore(() -> metadataEntityFieldApi.getFieldJdbcTypes(reqDTO));
        if (CollectionUtils.isNotEmpty(respDTOS)) {
            EntityFieldJdbcTypeRespDTO respDTO = respDTOS.get(0);
            return JdbcTypeEnum.getByCode(respDTO.getJdbcType());
        } else {
            throw new IllegalArgumentException("字段不存在:" + filedId);
        }
    }

}

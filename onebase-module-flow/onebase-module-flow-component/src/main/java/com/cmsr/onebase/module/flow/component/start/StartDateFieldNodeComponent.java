package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.framework.tenant.core.util.TenantUtils;
import com.cmsr.onebase.module.flow.component.data.DataMethodApiHelper;
import com.cmsr.onebase.module.flow.component.utils.ConditionsProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.condition.Conditions;
import com.cmsr.onebase.module.flow.context.enums.JdbcTypeEnum;
import com.cmsr.onebase.module.flow.context.enums.OpEnum;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.InLoopDepth;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.metadata.api.datamethod.DataMethodApi;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.ConditionDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataReqDTO;
import com.cmsr.onebase.module.metadata.api.datamethod.dto.EntityFieldDataRespDTO;
import com.cmsr.onebase.module.metadata.api.entity.MetadataEntityFieldApi;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeReqDTO;
import com.cmsr.onebase.module.metadata.api.entity.dto.EntityFieldJdbcTypeRespDTO;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("startDateField")
public class StartDateFieldNodeComponent extends NodeComponent {

    @Autowired
    private DataMethodApi dataMethodApi;

    @Autowired
    private ConditionsProvider conditionsProvider;

    @Autowired
    private MetadataEntityFieldApi metadataEntityFieldApi;

    @Override
    public void process() throws Exception {
        log.info("StartDateFieldNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
        StartDateFieldNodeData nodeData = (StartDateFieldNodeData) executeContext.getNodeData(this.getTag());
        InLoopDepth inLoopDepth = nodeData.getInLoopDepth();
        JdbcTypeEnum fieldJdbcType = queryFieldType(nodeData.getOffsetFiledId());

        EntityFieldDataReqDTO reqDTO = new EntityFieldDataReqDTO();
        reqDTO.setEntityId(nodeData.getEntityId());
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
            OrExpression orExpression = conditionsProvider.formatConditionsForValue(this, variableContext, inLoopDepth, conditions);
            reqDTO.setConditionDTO(DataMethodApiHelper.processFilterCondition(orExpression));
        }
        if (nodeData.isBatchMode()) {
            reqDTO.setNum(nodeData.getBatchSize());
        }
        List<List<EntityFieldDataRespDTO>> fieldDataRespDTOS = TenantUtils.executeIgnore(() -> dataMethodApi.getDataByCondition(reqDTO));
        variableContext.putNodeVariables(this.getTag(), DataMethodApiHelper.convertToListMap(fieldDataRespDTOS));
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

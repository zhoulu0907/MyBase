package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.context.express.ExpressionAssistant;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.field.FieldExpressAssistant;
import com.cmsr.onebase.module.flow.context.field.FieldInfo;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.data.StartEntityNodeData;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author：huangjie
 * @Date：2025/9/19 11:00
 */
@Slf4j
@Setter
@Service
public class FlowProcessExecApiImpl implements FlowProcessExecApi {

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private ExpressionAssistant expressionAssistant;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private FieldExpressAssistant fieldExpressAssistant;

    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO) {
        List<StartEntityNodeData> entityNodeDataList = graphFlowCache.getStartEntityNodeData(entityTriggerReqDTO.getEntityId());
        if (CollectionUtils.isEmpty(entityNodeDataList)) {
            return EntityTriggerRespDTO.SUCCESS;
        }
        for (StartEntityNodeData startEntityNodeData : entityNodeDataList) {
            EntityTriggerRespDTO entityTriggerRespDTO = entityTrigger(entityTriggerReqDTO, startEntityNodeData);
            if (!entityTriggerRespDTO.isSuccess()) {
                return entityTriggerRespDTO;
            }
        }
        return EntityTriggerRespDTO.SUCCESS;
    }

    private EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO, StartEntityNodeData startEntityNodeData) {
        try {
            if (!triggerEventContains(startEntityNodeData.getTriggerEvents(), entityTriggerReqDTO.getTriggerEvent())) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            if (!triggerFieldIdsContained(startEntityNodeData.getTriggerFieldIds(), entityTriggerReqDTO.getChangedFieldIds())) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            if (startEntityNodeData.getCompiledExpression() == null) {
                List<Long> ids = fieldExpressAssistant.extractFieldIds(startEntityNodeData.getFilterCondition());
                fieldExpressAssistant.convertToExpresses(startEntityNodeData.getFilterCondition(), fieldInfoMap)
                startEntityNodeData.getFilterCondition();
                Serializable compileExpression = expressionAssistant.compileExpression();
                startEntityNodeData.setCompiledExpression(compileExpression);
            }
            boolean isTrigger = expressionAssistant.evaluate(startEntityNodeData.getCompiledExpression(), entityTriggerReqDTO.getFieldData());
            if (!isTrigger) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            flowProcessExecutor.execute(startEntityNodeData.getProcessId(), entityTriggerReqDTO.getFieldData());
            return EntityTriggerRespDTO.SUCCESS;
        } catch (Exception e) {
            log.error("entityTrigger failed, {}, {}", entityTriggerReqDTO, startEntityNodeData, e);
            return new EntityTriggerRespDTO(false, e);
        }
    }

    private Map<Long, FieldInfo> getFieldInfoMap(List<Long> fieldIds) {
        EntityFieldJdbcTypeReqDTO reqDTO = new EntityFieldJdbcTypeReqDTO();
        reqDTO.setFieldIds(fieldIds);

        List<EntityFieldJdbcTypeRespDTO> fieldJdbcTypes = metadataEntityFieldApi.getFieldJdbcTypes(reqDTO);

        return fieldJdbcTypes.stream()
                .collect(Collectors.toMap(EntityFieldJdbcTypeRespDTO::getFieldId, info -> {
                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setFieldId(info.getFieldId());
                    fieldInfo.setFieldName(info.getFieldName());
                    fieldInfo.setJdbcType(info.getJdbcType());
                    return fieldInfo;
                }));
    }
    /**
     * 检查 triggerEvents 列表是否包含 triggerEvent 的名称（忽略大小写）
     *
     * @param triggerEvents
     * @param triggerEvent
     * @return
     */
    private boolean triggerEventContains(List<String> triggerEvents, TriggerEventEnum triggerEvent) {
        if (triggerEvents == null) {
            return false;
        }
        if (triggerEvent == null) {
            return false;
        }
        String eventName = triggerEvent.getCode();
        return triggerEvents.stream().anyMatch(event -> event.equalsIgnoreCase(eventName));
    }

    /**
     * 检查 triggerFieldIds 列表是否包含 changedFieldIds 中的全部元素，即changedFieldIds是否是triggerFieldIds的子集
     *
     * @param triggerFieldIds
     * @param changedFieldIds
     * @return
     */
    private boolean triggerFieldIdsContained(List<Long> triggerFieldIds, List<Long> changedFieldIds) {
        if (CollectionUtils.isEmpty(triggerFieldIds)) {
            return true;
        }
        if (CollectionUtils.isNotEmpty(triggerFieldIds) && CollectionUtils.isEmpty(changedFieldIds)) {
            return false;
        }
        return changedFieldIds.stream().allMatch(changedId -> triggerFieldIds.contains(changedId));
    }


}

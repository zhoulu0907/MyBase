package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.graph.data.StartEntityNodeData;
import com.cmsr.onebase.module.flow.core.rule.ExpressionAssistant;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

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

    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO) {
        Optional<StartEntityNodeData> nodeDataOptional = graphFlowCache.getStartEntityNodeData(entityTriggerReqDTO.getEntityId());
        if (!nodeDataOptional.isPresent()) {
            return EntityTriggerRespDTO.SUCCESS;
        }
        try {
            StartEntityNodeData startEntityNodeData = nodeDataOptional.get();
            if (!triggerEventContains(startEntityNodeData.getTriggerEvents(), entityTriggerReqDTO.getTriggerEvent())) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            if (!triggerFieldIdsContained(startEntityNodeData.getTriggerFieldIds(), entityTriggerReqDTO.getChangedFieldIds())) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            if (startEntityNodeData.getCompiledExpression() == null) {
                Serializable compileExpression = expressionAssistant.compileExpression(startEntityNodeData.getFilterCondition());
                startEntityNodeData.setCompiledExpression(compileExpression);
            }
            boolean isTrigger = expressionAssistant.evaluate(startEntityNodeData.getCompiledExpression(), entityTriggerReqDTO.getFieldData());
            if (!isTrigger) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            flowProcessExecutor.execute(startEntityNodeData.getProcessId(), entityTriggerReqDTO.getFieldData());
            return EntityTriggerRespDTO.SUCCESS;
        } catch (Exception e) {
            log.error("entityTrigger failed, {}, {}", nodeDataOptional, entityTriggerReqDTO, e);
            return new EntityTriggerRespDTO(false, e);
        }
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

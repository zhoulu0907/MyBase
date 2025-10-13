package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.condition.ConditionsSupport;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartEntityNodeData;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    private ExpressionExecutor expressionExecutor;

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;


    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO) {
        List<StartEntityNodeData> entityNodeDataList = graphFlowCache.findStartEntityNodeDataByEntityId(entityTriggerReqDTO.getEntityId());
        if (CollectionUtils.isEmpty(entityNodeDataList)) {
            return EntityTriggerRespDTO.SUCCESS;
        }
        EntityTriggerRespDTO errorEntityTriggerRespDTO = null;
        for (StartEntityNodeData startEntityNodeData : entityNodeDataList) {
            EntityTriggerRespDTO entityTriggerRespDTO = entityTrigger(entityTriggerReqDTO, startEntityNodeData);
            if (!entityTriggerRespDTO.isSuccess()) {
                errorEntityTriggerRespDTO = entityTriggerRespDTO;
            }
        }
        if (errorEntityTriggerRespDTO != null) {
            return errorEntityTriggerRespDTO;
        } else {
            return EntityTriggerRespDTO.SUCCESS;
        }
    }

    private EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO entityTriggerReqDTO, StartEntityNodeData startEntityNodeData) {
        try {
            if (!triggerEventContains(startEntityNodeData.getTriggerEvents(), entityTriggerReqDTO.getTriggerEvent())) {
                return EntityTriggerRespDTO.SUCCESS;
            }
            if (CollectionUtils.isNotEmpty(startEntityNodeData.getFilterCondition())) {
                OrExpression orExpression = ConditionsSupport.convertToOrExpresses(startEntityNodeData.getFilterCondition());
                boolean isMatch = expressionExecutor.evaluate(orExpression, entityTriggerReqDTO.getFieldData());
                if (!isMatch) {
                    return EntityTriggerRespDTO.SUCCESS;
                }
            }
            ExecutorResult executorResult = flowProcessExecutor.execute(startEntityNodeData.getProcessId(), entityTriggerReqDTO.getFieldData());
            EntityTriggerRespDTO resp = new EntityTriggerRespDTO();
            resp.setSuccess(executorResult.isSuccess());
            resp.setCode(executorResult.getCode());
            resp.setMessage(executorResult.getMessage());
            resp.setCause(executorResult.getCause());
            resp.setExecutionEnd(executorResult.isExecutionEnd());
            return resp;
        } catch (Exception e) {
            log.error("entityTrigger failed, {}, {}", entityTriggerReqDTO, startEntityNodeData, e);
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
        String eventCode = triggerEvent.getCode();
        return triggerEvents.stream().anyMatch(event -> event.equalsIgnoreCase(eventCode));
    }


}

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

import java.util.ArrayList;
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
    private FlowProcessExecutor flowProcessExecutor;


    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO reqDTO) {
        List<StartEntityNodeData> entityNodeDataList = graphFlowCache.findStartEntityNodeDataByEntityId(reqDTO.getEntityId());
        if (CollectionUtils.isEmpty(entityNodeDataList)) {
            return new EntityTriggerRespDTO(reqDTO.getTraceId(), true, "没有相应的流程");
        }
        List<EntityTriggerRespDTO> respDTOS = new ArrayList<>();
        for (StartEntityNodeData startEntityNodeData : entityNodeDataList) {
            EntityTriggerRespDTO entityTriggerRespDTO = entityTrigger(reqDTO, startEntityNodeData);
            if (!entityTriggerRespDTO.isSuccess()) {
                return entityTriggerRespDTO;
            } else {
                respDTOS.add(entityTriggerRespDTO);
            }
        }
        if (respDTOS.size() == 1) {
            return respDTOS.get(0);
        } else {
            return new EntityTriggerRespDTO(reqDTO.getTraceId(), true, "执行结束: " + respDTOS);
        }
    }

    private EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO reqDTO, StartEntityNodeData nodeData) {
        try {
            if (!triggerEventContains(nodeData.getTriggerEvents(), reqDTO.getTriggerEvent())) {
                return new EntityTriggerRespDTO(reqDTO.getTraceId(), nodeData.getProcessId(), true, "触发事件不匹配");
            }
            if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
                OrExpression orExpression = ConditionsSupport.convertToOrExpresses(nodeData.getFilterCondition());
                boolean isMatch = expressionExecutor.evaluate(orExpression, reqDTO.getFieldData());
                if (!isMatch) {
                    return new EntityTriggerRespDTO(reqDTO.getTraceId(), nodeData.getProcessId(), true, "触发条件不匹配");
                }
            }
            ExecutorResult executorResult = flowProcessExecutor.execute(
                    reqDTO.getTraceId(),
                    nodeData.getProcessId(),
                    reqDTO.getFieldData());
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setProcessId(executorResult.getProcessId());
            respDTO.setSuccess(executorResult.isSuccess());
            respDTO.setCode(executorResult.getCode());
            respDTO.setMessage(executorResult.getMessage());
            respDTO.setCause(executorResult.getCause());
            respDTO.setExecutionEnd(executorResult.isExecutionEnd());
            return respDTO;
        } catch (Exception e) {
            log.error("entityTrigger failed, {}, {}", reqDTO, nodeData, e);
            return new EntityTriggerRespDTO(reqDTO.getTraceId(), nodeData.getProcessId(), false, e);
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

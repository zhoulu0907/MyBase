package com.cmsr.onebase.module.flow.api;

import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.FlowProcessCache;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartEntityNodeData;
import com.cmsr.onebase.module.flow.context.provider.ConditionsProvider;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/19 11:00
 */
@Slf4j
@Setter
@Service
public class FlowProcessExecApiImpl implements FlowProcessExecApi {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private ConditionsProvider conditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO reqDTO) {
        List<StartEntityNodeData> entityNodeDataList = FlowProcessCache.findStartEntityNodeDataByEntityName(reqDTO.getApplicationId(), reqDTO.getTableName());
        if (CollectionUtils.isEmpty(entityNodeDataList)) {
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setSuccess(true);
            respDTO.setTriggered(false);
            respDTO.setMessage("实体未配置流程:" + reqDTO.getTableName());
            return respDTO;
        }
        List<EntityTriggerRespDTO> respDTOS = new ArrayList<>();
        boolean success = true;
        boolean triggered = false;
        for (StartEntityNodeData startEntityNodeData : entityNodeDataList) {
            EntityTriggerRespDTO respDTO = entityTrigger(reqDTO, startEntityNodeData);
            if (!respDTO.isSuccess()) {
                success = false;
            }
            if (respDTO.isTriggered()) {
                triggered = true;
            }
            respDTOS.add(respDTO);
        }
        if (respDTOS.size() == 1) {
            return respDTOS.get(0);
        } else {
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setSuccess(success);
            respDTO.setTriggered(triggered);
            respDTO.setMessage(String.format("执行结束, 尝试%s个流程", respDTOS.size()));
            respDTO.setDetail(respDTOS.toString());
            return respDTO;
        }
    }

    private EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO reqDTO, StartEntityNodeData nodeData) {
        Map<String, Object> inputData = new HashMap<>();
        for (Map.Entry<?, Object> entry : reqDTO.getFieldData().entrySet()) {
            inputData.put(entry.getKey().toString(), entry.getValue());
        }
        EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId(), nodeData.getProcessId());
        try {
            if (!triggerEventContains(nodeData.getTriggerEvents(), reqDTO.getTriggerEvent())) {
                respDTO.setSuccess(true);
                respDTO.setTriggered(false);
                respDTO.setMessage(String.format("不支持的触发事件, 配置: %s, 传入: %s.", nodeData.getTriggerEvents(), reqDTO.getTriggerEvent().getCode()));
                return respDTO;
            }
            if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
                OrExpression orExpression = conditionsProvider.formatConditionsForExpression(nodeData.getFilterCondition(), inputData);
                boolean isMatch = expressionExecutor.evaluate(orExpression, inputData);
                if (!isMatch) {
                    respDTO.setSuccess(true);
                    respDTO.setTriggered(false);
                    respDTO.setMessage(String.format("触发条件不匹配: %s", orExpression));
                    return respDTO;
                }
            }
            respDTO.setTriggered(true);
            ExecutorResult executorResult = flowProcessExecutor.execute(
                    reqDTO.getTraceId(),
                    nodeData.getProcessId(),
                    inputData);
            respDTO.setSuccess(executorResult.isSuccess());
            respDTO.setCode(executorResult.getCode());
            respDTO.setMessage(executorResult.getMessage());
            respDTO.setCause(executorResult.getCause());
            respDTO.setExecutionEnd(executorResult.isExecutionEnd());
            return respDTO;
        } catch (Exception e) {
            log.error("entityTrigger failed, {}, {}", reqDTO, nodeData, e);
            respDTO.setSuccess(false);
            respDTO.setCause(e);
            return respDTO;
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

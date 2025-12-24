package com.cmsr.onebase.module.flow.core.impl;

import com.cmsr.onebase.module.flow.api.FlowProcessExecApi;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerReqDTO;
import com.cmsr.onebase.module.flow.api.dto.EntityTriggerRespDTO;
import com.cmsr.onebase.module.flow.api.dto.TriggerEventEnum;
import com.cmsr.onebase.module.flow.context.express.ExpressionExecutor;
import com.cmsr.onebase.module.flow.context.express.OrExpression;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartEntityNodeData;
import com.cmsr.onebase.module.flow.context.provider.FlowConditionsProvider;
import com.cmsr.onebase.module.flow.core.flow.ExecutorInput;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.metadata.core.semantic.constants.SystemFieldConstants;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private ObjectProvider<FlowProcessExecutor> flowProcessExecutorObjectProvider;

    @Autowired
    private FlowConditionsProvider flowConditionsProvider;

    private ExpressionExecutor expressionExecutor = new ExpressionExecutor();

    private FlowProcessCache flowProcessCache = FlowProcessCache.getInstance();

    @Override
    public EntityTriggerRespDTO entityTrigger(EntityTriggerReqDTO reqDTO) {
        log.info("entityTrigger req: {}", reqDTO);
        try {
            EntityTriggerRespDTO respDTO = doEntityTrigger(reqDTO);
            log.info("entityTrigger resp: {}", respDTO);
            return respDTO;
        } catch (Exception e) {
            log.error("entityTrigger error: {}", reqDTO, e);
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setSuccess(false);
            respDTO.setTriggered(false);
            respDTO.setMessage("执行异常");
            respDTO.setCause(e);
            return respDTO;
        }
    }

    private EntityTriggerRespDTO doEntityTrigger(EntityTriggerReqDTO reqDTO) {
        if (reqDTO.getApplicationId() == null) {
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setSuccess(false);
            respDTO.setTriggered(false);
            respDTO.setMessage("应用ID不能为空");
            return respDTO;
        }
        if (reqDTO.getTableName() == null) {
            EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId());
            respDTO.setSuccess(false);
            respDTO.setTriggered(false);
            respDTO.setMessage("实体名称不能为空");
            return respDTO;
        }

        List<StartEntityNodeData> entityNodeDataList = flowProcessCache.findStartEntityNodeDataByEntityName(reqDTO.getApplicationId(), reqDTO.getTableName());
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
        FlowProcessExecutor flowProcessExecutor = flowProcessExecutorObjectProvider.getIfAvailable();
        Map<String, Object> inputData = reqDTO.toInputData();
        EntityTriggerRespDTO respDTO = new EntityTriggerRespDTO(reqDTO.getTraceId(), nodeData.getProcessId());
        //先判断
        if (flowProcessExecutor == null) {
            respDTO.setMessage("流程执行器未初始化");
            return respDTO;
        }
        try {
            if (!triggerEventContains(nodeData.getTriggerEvents(), reqDTO.getTriggerEvent())) {
                respDTO.setSuccess(true);
                respDTO.setTriggered(false);
                respDTO.setMessage(String.format("不支持的触发事件, 配置: %s, 传入: %s.", nodeData.getTriggerEvents(), reqDTO.getTriggerEvent().getCode()));
                return respDTO;
            }
            if (CollectionUtils.isNotEmpty(nodeData.getFilterCondition())) {
                OrExpression orExpression = flowConditionsProvider.formatConditionsForExpression(nodeData.getFilterCondition(), inputData);
                boolean isMatch = expressionExecutor.evaluateInput(orExpression, inputData);
                if (!isMatch) {
                    respDTO.setSuccess(true);
                    respDTO.setTriggered(false);
                    respDTO.setMessage(String.format("触发条件不匹配: %s", orExpression));
                    return respDTO;
                }
            }
            respDTO.setTriggered(true);
            ExecutorInput executorInput = new ExecutorInput();
            executorInput.setTraceId(reqDTO.getTraceId());
            executorInput.setProcessId(nodeData.getProcessId());
            executorInput.setInputParams(inputData);
            executorInput.setTriggerUserId(MapUtils.getLong(reqDTO.getFlowContext(), SystemFieldConstants.REQUIRE.CREATOR));
            executorInput.setSystemFields(reqDTO.getFlowContext());

            ExecutorResult executorResult = flowProcessExecutor.startExecution(executorInput);
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

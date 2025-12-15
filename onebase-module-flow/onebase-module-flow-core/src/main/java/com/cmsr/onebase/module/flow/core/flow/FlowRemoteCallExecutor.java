package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.external.FlowSystemProvider;
import com.cmsr.onebase.module.flow.core.config.FlowEnableCondition;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/3 14:35
 */
@Setter
@Slf4j
@Component
@Conditional(FlowEnableCondition.class)
public class FlowRemoteCallExecutor {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private FlowSystemProvider flowSystemProvider;

    public ExecutorResult executeFlow(FlowRemoteCallRequest jobMessage) {
        ExecutorResult executorResult;
        try {
            Map<String, Object> inputParams;
            if (FlowRemoteCallRequest.JOB_TYPE_FIELD.equals(jobMessage.getJobType())) {
                inputParams = createDateFieldInputParams(jobMessage);
            } else if (FlowRemoteCallRequest.JOB_TYPE_TIME.equals(jobMessage.getJobType())) {
                inputParams = createTimeInputParams(jobMessage);
            } else {
                return ExecutorResult.error("未知的流程类型:" + jobMessage.getJobType());

            }
            log.info("处理流程消息: {}", jobMessage);
            FlowProcessDO flowProcessDO = FlowProcessCache.findProcessByProcessId(jobMessage.getProcessId());
            if (flowProcessDO == null) {
                return ExecutorResult.error("流程不存在:" + jobMessage.getProcessId());
            }
            ExecutorInput executorInput = createExecutorInput(flowProcessDO, inputParams);
            executorResult = flowProcessExecutor.startExecution(executorInput);
            log.error("执行流程结果：{}", executorResult);
        } catch (Exception e) {
            log.error("处理流程消息异常：{}", e.getMessage(), e);
            executorResult = new ExecutorResult();
            executorResult.setSuccess(false);
            executorResult.setCause(e);
        }
        return executorResult;
    }

    private ExecutorInput createExecutorInput(FlowProcessDO flowProcessDO, Map<String, Object> inputParams) {
        Long userDeptId = flowSystemProvider.findUserDeptId(flowProcessDO.getCreator());
        ExecutorInput executorInput = new ExecutorInput();
        executorInput.setTraceId(FlowUtils.generateTraceId());
        executorInput.setProcessId(flowProcessDO.getId());
        executorInput.setTriggerUserId(flowProcessDO.getCreator());
        executorInput.setTriggerUserDeptId(userDeptId);
        executorInput.setInputParams(inputParams);
        return executorInput;
    }

    private Map<String, Object> createDateFieldInputParams(FlowRemoteCallRequest message) {
        Long processId = message.getProcessId();
        StartDateFieldNodeData startDateFieldNodeData = FlowProcessCache.findStartDateFieldNodeDataByProcessId(processId);
        if (startDateFieldNodeData == null) {
            throw new RuntimeException("实体时间字段触发流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> createTimeInputParams(FlowRemoteCallRequest message) {
        Long processId = message.getProcessId();
        StartTimeNodeData startTimeNodeData = FlowProcessCache.findStartTimeNodeDataByProcessId(processId);
        if (startTimeNodeData == null) {
            throw new RuntimeException("定时任务流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

}

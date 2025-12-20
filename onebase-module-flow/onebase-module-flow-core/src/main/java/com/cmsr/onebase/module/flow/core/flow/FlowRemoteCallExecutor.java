package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.framework.common.security.TenantContextHolder;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.start.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowEnableCondition;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowProcessDO;
import com.cmsr.onebase.module.flow.core.external.FlowSystemProvider;
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

    private FlowProcessCache flowProcessCache = FlowProcessCache.getInstance();


    public ExecutorResult executeFlow(FlowRemoteCallRequest callRequest) {
        ExecutorResult executorResult;
        try {
            Map<String, Object> inputParams;
            if (FlowRemoteCallRequest.JOB_TYPE_FIELD.equals(callRequest.getJobType())) {
                inputParams = createDateFieldInputParams(callRequest);
            } else if (FlowRemoteCallRequest.JOB_TYPE_TIME.equals(callRequest.getJobType())) {
                inputParams = createTimeInputParams(callRequest);
            } else {
                return ExecutorResult.error("未知的流程类型:" + callRequest.getJobType());

            }
            log.info("处理流程消息: {}", callRequest);
            FlowProcessDO flowProcessDO = flowProcessCache.findProcessByProcessId(callRequest.getProcessId());
            if (flowProcessDO == null) {
                return ExecutorResult.error("流程不存在:" + callRequest.getProcessId());
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

        ExecutorInput executorInput = new ExecutorInput();
        executorInput.setTraceId(FlowUtils.generateTraceId());
        executorInput.setProcessId(flowProcessDO.getId());
        executorInput.setTriggerUserId(flowProcessDO.getCreator());
        executorInput.setInputParams(inputParams);
        try {
            //todo 忽略租户条件，等待删除
            TenantContextHolder.setIgnore(true);
            Long userDeptId = flowSystemProvider.findUserDeptId(flowProcessDO.getCreator());
            executorInput.setTriggerUserDeptId(userDeptId);
        } catch (Exception e) {
            log.warn("获取用户部门信息异常：{}", e.getMessage(), e);
        }
        return executorInput;
    }

    private Map<String, Object> createDateFieldInputParams(FlowRemoteCallRequest message) {
        Long processId = message.getProcessId();
        StartDateFieldNodeData startDateFieldNodeData = flowProcessCache.findStartDateFieldNodeDataByProcessId(processId);
        if (startDateFieldNodeData == null) {
            throw new RuntimeException("实体时间字段触发流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> createTimeInputParams(FlowRemoteCallRequest message) {
        Long processId = message.getProcessId();
        StartTimeNodeData startTimeNodeData = flowProcessCache.findStartTimeNodeDataByProcessId(processId);
        if (startTimeNodeData == null) {
            throw new RuntimeException("定时任务流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

}

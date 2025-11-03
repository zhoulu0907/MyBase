package com.cmsr.onebase.module.flow.core.job;

import com.cmsr.onebase.module.flow.context.graph.nodes.StartDateFieldNodeData;
import com.cmsr.onebase.module.flow.context.graph.nodes.StartTimeNodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.flow.ExecutorResult;
import com.cmsr.onebase.module.flow.core.flow.FlowProcessExecutor;
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
@Conditional(FlowRuntimeCondition.class)
public class FlowJobExecutor {

    @Autowired
    private FlowProcessExecutor flowProcessExecutor;

    @Autowired
    private FlowProcessCache flowProcessCache;

    public ExecutorResult executeFlow(FlowJobMessage jobMessage) {
        ExecutorResult executorResult;
        try {
            Map<String, Object> inputParams;
            if (jobMessage.getJobType().equals("fld")) {
                inputParams = createDateFieldInputParams(jobMessage);
            } else {
                inputParams = createTimeInputParams(jobMessage);
            }
            log.info("处理流程消息: {}", jobMessage);
            executorResult = flowProcessExecutor.execute(FlowUtils.generateTraceId(), jobMessage.getProcessId(), inputParams);
            log.error("执行流程结果：{}", executorResult);
        } catch (Exception e) {
            log.error("处理流程消息异常：{}", e.getMessage(), e);
            executorResult = new ExecutorResult();
            executorResult.setSuccess(false);
            executorResult.setCause(e);
        }
        return executorResult;
    }

    private Map<String, Object> createDateFieldInputParams(FlowJobMessage message) {
        Long processId = message.getProcessId();
        StartDateFieldNodeData startDateFieldNodeData = flowProcessCache.findStartDateFieldNodeDataByProcessId(processId);
        if (startDateFieldNodeData == null) {
            throw new RuntimeException("实体时间字段触发流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

    private Map<String, Object> createTimeInputParams(FlowJobMessage message) {
        Long processId = message.getProcessId();
        StartTimeNodeData startTimeNodeData = flowProcessCache.findStartTimeNodeDataByProcessId(processId);
        if (startTimeNodeData == null) {
            throw new RuntimeException("定时任务流程未找到:" + processId);
        }
        return Collections.emptyMap();
    }

}

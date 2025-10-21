package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.ContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowExecutionLogRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Author：huangjie
 * @Date：2025/9/11 14:32
 */
@Setter
@Slf4j
@Component
@Conditional(FlowRuntimeCondition.class)
public class FlowProcessExecutor {

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private GraphFlowCache graphFlowCache;

    @Autowired
    private ContextProvider contextProvider;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private FlowExecutionLogRepository flowExecutionLogRepository;

    /**
     * 执行新流程（基于traceId和输入参数）
     */
    public ExecutorResult execute(String traceId, Long processId, Map<String, Object> inputParams) {
        if (!graphFlowCache.isProcessExist(processId)) {
            return ExecutorResult.error("流程不存在: " + processId);
        }
        FlowExecutionLogDO executionLog = createNewExecutionLog(processId);
        return executeWithLogging(() -> executeNewFlow(traceId, processId, inputParams), executionLog);
    }

    /**
     * 恢复执行流程（基于执行UUID）
     */
    public ExecutorResult execute(Long processId, String executionUuid, Map<String, Object> inputFields) {
        if (!graphFlowCache.isProcessExist(processId)) {
            return ExecutorResult.error("流程不存在: " + processId);
        }
        FlowExecutionLogDO executionLog = findOrCreateExecutionLog(processId, executionUuid);
        return executeWithLogging(() -> resumeFlowExecution(processId, executionUuid, inputFields), executionLog);
    }

    /**
     * 执行新流程的核心逻辑
     */
    private ExecutorResult executeNewFlow(String traceId, Long processId, Map<String, Object> inputParams) {
        Map<String, NodeData> nodeData = graphFlowCache.findNodeData(processId);

        traceId = validateAndGenerateTraceId(traceId, processId);

        VariableContext variableContext = new VariableContext();
        variableContext.setInputParams(inputParams);

        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setTraceId(traceId);
        executeContext.setProcessId(processId);
        executeContext.setNodeDataMap(nodeData);

        return executeFlow(processId, variableContext, executeContext);
    }

    /**
     * 恢复流程执行的核心逻辑
     */
    private ExecutorResult resumeFlowExecution(Long processId, String executionUuid, Map<String, Object> inputFields) {
        VariableContext variableContext = contextProvider.restoreVariableContext(executionUuid);
        if (variableContext == null) {
            return ExecutorResult.error("执行上下文不存在或已过期: " + executionUuid);
        }

        variableContext.setInputFields(inputFields);
        variableContext.setOutputParams(Collections.emptyMap());

        ExecuteContext executeContext = contextProvider.restoreExecuteContext(executionUuid);
        if (executeContext == null) {
            return ExecutorResult.error("执行上下文不存在或已过期: " + executionUuid);
        }

        return executeFlow(processId, variableContext, executeContext);
    }

    /**
     * 执行流程的公共逻辑
     */
    private ExecutorResult executeFlow(Long processId, VariableContext variableContext, ExecuteContext executeContext) {
        String chainId = FlowUtils.toFlowChainId(processId);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);

        VariableContext updatedVariableContext = response.getContextBean(VariableContext.class);
        ExecuteContext updatedExecuteContext = response.getContextBean(ExecuteContext.class);

        return buildExecutorResult(response, updatedExecuteContext, updatedVariableContext);
    }

    /**
     * 带日志记录的执行包装器
     */
    private ExecutorResult executeWithLogging(Supplier<ExecutorResult> execution, FlowExecutionLogDO executionLog) {
        try {
            ExecutorResult result = execution.get();

            executionLog.setExecutionUuid(result.getExecutionUuid());
            executionLog.setExecutionResult(result.isSuccess() ? "success" : "failed");
            executionLog.setErrorMessage(ExceptionUtils.getRootCauseMessage(result.getCause()));
            return result;
        } catch (Exception e) {
            log.error("执行流程异常", e);
            executionLog.setExecutionResult("failed");
            executionLog.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            return ExecutorResult.error("执行流程异常", e);
        } finally {
            executionLog.setEndTime(LocalDateTime.now());
            flowExecutionLogRepository.insert(executionLog);
        }
    }


    /**
     * 验证并生成traceId
     */
    private String validateAndGenerateTraceId(String traceId, Long processId) {
        if (StringUtils.isEmpty(traceId)) {
            return FlowUtils.generateTraceId();
        }
        List<Long> callInvocation = queryCallInvocation(traceId, processId);
        if (callInvocation.size() > FlowUtils.MAX_QUERY_CALL_COUNT) {
            throw new IllegalStateException("触发流程执行次数超阈值[" + traceId + "][" + callInvocation + "]");
        }
        return traceId;
    }


    /**
     * 创建新的执行日志
     */
    private FlowExecutionLogDO createNewExecutionLog(Long processId) {
        FlowExecutionLogDO log = new FlowExecutionLogDO();
        log.setProcessId(processId);
        log.setStartTime(LocalDateTime.now());
        return log;
    }

    /**
     * 查找或创建执行日志
     */
    private FlowExecutionLogDO findOrCreateExecutionLog(Long processId, String executionUuid) {
        FlowExecutionLogDO log = flowExecutionLogRepository.findByExecutionUuid(executionUuid);
        if (log == null) {
            log = createNewExecutionLog(processId);
        }
        return log;
    }

    /**
     * 查询调用次数
     */
    private List<Long> queryCallInvocation(String traceId, Long processId) {
        if (StringUtils.isEmpty(traceId)) {
            throw new IllegalArgumentException("traceId is empty");
        }
        String key = FlowUtils.toRedisTraceKey(traceId);
        RList<Long> list = redissonClient.getList(key);
        list.expire(FlowUtils.REDIS_TRACE_TIMEOUT);
        list.add(processId);
        return new ArrayList<>(list);
    }

    /**
     * 构建执行结果
     */
    private static ExecutorResult buildExecutorResult(LiteflowResponse response, ExecuteContext executeContext, VariableContext variableContext) {
        ExecutorResult result = new ExecutorResult();
        result.setSuccess(response.isSuccess());
        result.setCode(response.getCode());
        result.setMessage(response.getMessage());
        result.setCause(response.getCause());
        result.setExecutionEnd(executeContext.isExecuteEnd());
        result.setExecutionUuid(executeContext.getExecutionUuid());
        result.setExecutionEndNodeType(executeContext.getExecutionEndNodeType());
        result.setExecutionEndNodeTag(executeContext.getExecutionEndNodeTag());
        result.setOutputParams(variableContext.getOutputParams());
        return result;
    }

    // 使用Java标准库中的Supplier<ExecutorResult>替代自定义的FlowExecution接口


}

package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.ContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.dal.database.FlowExecutionLogRepository;
import com.cmsr.onebase.module.flow.core.dal.dataobject.FlowExecutionLogDO;
import com.cmsr.onebase.module.flow.core.enums.ExecutionResultEnum;
import com.cmsr.onebase.module.flow.core.graph.FlowProcessCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.google.common.collect.Lists;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private FlowProcessCache flowProcessCache;

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
        if (!flowProcessCache.isProcessExist(processId)) {
            return ExecutorResult.error(processId, "流程不存在: " + processId);
        }
        if (StringUtils.isEmpty(traceId)) {
            traceId = FlowUtils.generateTraceId();
        }
        FlowExecutionLogDO executionLog = createNewExecutionLog(processId);
        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setProcessId(processId);
        try {
            //初始化变量上下文
            VariableContext variableContext = new VariableContext();
            variableContext.setInputParams(inputParams);
            //初始化执行上下文
            Map<String, NodeData> nodeData = flowProcessCache.findNodeData(processId);
            executeContext.setNodeDataMap(nodeData);

            executeContext.addLog("检查流程触发次数");
            int callCount = validateTraceIdCallCount(traceId, processId);
            executeContext.addLog("流程触发阈值正常[" + callCount + "]");
            executeContext.setTraceId(traceId);
            //执行上下文添加执行UUID
            executeContext.setExecutionUuid(UUID.randomUUID().toString());
            //设置日志执行UUID
            executionLog.setTraceId(executeContext.getTraceId());
            executionLog.setExecutionUuid(executeContext.getExecutionUuid());
            //执行流程
            ExecutorResult result = executeFlow(processId, variableContext, executeContext);
            //处理结果到日志
            executionLog.setExecutionResult(result.isSuccess() ? ExecutionResultEnum.SUCCESS.getCode() : ExecutionResultEnum.FAILED.getCode());
            executionLog.setErrorMessage(ExceptionUtils.getMessage(result.getCause()));
            return result;
        } catch (Exception e) {
            log.error("执行流程异常", e);
            executionLog.setExecutionResult(ExecutionResultEnum.FAILED.getCode());
            executionLog.setErrorMessage(ExceptionUtils.getMessage(e));
            return ExecutorResult.error(processId, "执行流程异常", e);
        } finally {
            executeContext.addLog("流程执行结束");
            executionLog.setLogText(executeContext.getLogText());
            executionLog.setEndTime(LocalDateTime.now());
            Duration duration = Duration.between(executionLog.getStartTime(), executionLog.getEndTime());
            executionLog.setDurationTime(duration.toMillis());
            flowExecutionLogRepository.insert(executionLog);
        }
    }

    /**
     * 恢复执行流程（基于执行UUID）
     * 恢复执行的trace id相同
     * 恢复执行的执行UUID不同
     * 理论上可以根据trace id 恢复完整的执行情况
     */
    public ExecutorResult execute(Long processId, String executionUuid, Map<String, Object> inputFields) {
        if (!flowProcessCache.isProcessExist(processId)) {
            return ExecutorResult.error(processId, "流程不存在: " + processId);
        }
        FlowExecutionLogDO executionLog = createNewExecutionLog(processId);
        ExecuteContext executeContext = new ExecuteContext();
        try {
            //初始化变量上下文
            executeContext.addLog("恢复变量上下文");
            VariableContext variableContext = contextProvider.restoreVariableContext(executionUuid);
            executeContext.addLog("恢复变量上下文结束");
            if (variableContext == null) {
                throw new Exception("执行上下文不存在或已过期: " + executionUuid);
            }
            Map<String, NodeData> nodeData = flowProcessCache.findNodeData(processId);
            executeContext.setNodeDataMap(nodeData);

            variableContext.setInputFields(inputFields);
            variableContext.setOutputParams(Collections.emptyMap());
            //初始化执行上下文
            executeContext.addLog("恢复执行上下文");
            executeContext = contextProvider.restoreExecuteContext(executionUuid);
            executeContext.addLog("恢复执行上下文结束");
            if (executeContext == null) {
                throw new Exception("执行上下文不存在或已过期: " + executionUuid);
            }
            //重置执行结果
            executeContext.resetNodeProcessResult();
            //执行上下文添加执行UUID
            executeContext.setExecutionUuid(UUID.randomUUID().toString());
            //设置日志执行UUID
            executionLog.setTraceId(executeContext.getTraceId());
            executionLog.setExecutionUuid(executeContext.getExecutionUuid());
            //执行流程
            ExecutorResult result = executeFlow(processId, variableContext, executeContext);
            //处理结果到日志
            executionLog.setExecutionResult(result.isSuccess() ? ExecutionResultEnum.SUCCESS.getCode() : ExecutionResultEnum.FAILED.getCode());
            executionLog.setErrorMessage(ExceptionUtils.getMessage(result.getCause()));
            return result;
        } catch (Exception e) {
            log.error("执行流程异常", e);
            executionLog.setExecutionResult(ExecutionResultEnum.FAILED.getCode());
            executionLog.setErrorMessage(ExceptionUtils.getMessage(e));
            return ExecutorResult.error(processId, "执行流程异常", e);
        } finally {
            executeContext.addLog("流程执行结束");
            executionLog.setLogText(executeContext.getLogText());
            executionLog.setEndTime(LocalDateTime.now());
            Duration duration = Duration.between(executionLog.getStartTime(), executionLog.getEndTime());
            executionLog.setDurationTime(duration.toMillis());
            flowExecutionLogRepository.insert(executionLog);
        }
    }


    /**
     * 执行流程的公共逻辑
     */
    private ExecutorResult executeFlow(Long processId, VariableContext variableContext, ExecuteContext executeContext) {
        String chainId = FlowUtils.toFlowChainId(processId);
        executeContext.addLog("调用流程执行方法");
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        executeContext.addLog("调用流程执行方法返回");
        VariableContext updatedVariableContext = response.getContextBean(VariableContext.class);
        ExecuteContext updatedExecuteContext = response.getContextBean(ExecuteContext.class);
        return buildExecutorResult(response, updatedExecuteContext, updatedVariableContext);
    }


    /**
     * 验证并生成traceId
     */
    private int validateTraceIdCallCount(String traceId, Long processId) {
        List<Long> callInvocation = queryCallInvocation(traceId, processId);
        if (callInvocation.size() > FlowUtils.MAX_QUERY_CALL_COUNT) {
            throw new IllegalStateException("触发流程执行次数超阈值[" + traceId + "][" + callInvocation + "]");
        }
        return callInvocation.size();
    }


    /**
     * 创建新的执行日志
     */
    private FlowExecutionLogDO createNewExecutionLog(Long processId) {
        Long applicationId = flowProcessCache.findApplicationByProcessId(processId);
        FlowExecutionLogDO log = new FlowExecutionLogDO();
        log.setApplicationId(applicationId);
        log.setProcessId(processId);
        log.setStartTime(LocalDateTime.now());
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
        RList<Long> list = redissonClient.getList(key, FlowUtils.KRYO5_CODEC);
        list.expire(FlowUtils.REDIS_TRACE_TIMEOUT);
        list.add(processId);
        return Lists.newArrayList(list);
    }

    /**
     * 构建执行结果
     */
    private static ExecutorResult buildExecutorResult(LiteflowResponse response, ExecuteContext executeContext, VariableContext variableContext) {
        ExecutorResult result = new ExecutorResult();
        result.setTraceId(executeContext.getTraceId());
        result.setProcessId(executeContext.getProcessId());
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

}

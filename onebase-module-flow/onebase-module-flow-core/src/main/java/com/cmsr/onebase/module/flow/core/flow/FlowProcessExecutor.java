package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.ContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.NodeData;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/11 14:32
 */
@Setter
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

    public ExecutorResult execute(String traceId, Long processId, Map<String, Object> inputParams) {
        Map<String, NodeData> nodeData = graphFlowCache.findNodeData(processId);
        if (nodeData == null) {
            throw new IllegalArgumentException("流程不存在: " + processId);
        }
        if (StringUtils.isEmpty(traceId)) {
            traceId = FlowUtils.generateTraceId();
        } else {
            List<Long> callInvocation = queryCallInvocation(traceId, processId);
            if (callInvocation.size() > FlowUtils.MAX_QUERY_CALL_COUNT) {
                throw new IllegalStateException("触发流程执行次数超阈值: [" + traceId + "][" + callInvocation + "]");
            }
        }
        // 变量上下文
        VariableContext variableContext = new VariableContext();
        variableContext.setInputParams(inputParams);
        // 执行上下文
        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setTraceId(traceId);
        executeContext.setProcessId(processId);
        executeContext.setNodeDataMap(nodeData);
        // 执行流程
        String chainId = FlowUtils.toFlowChainId(processId);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        variableContext = response.getContextBean(VariableContext.class);
        executeContext = response.getContextBean(ExecuteContext.class);
        ExecutorResult executorResult = getExecutorResult(response, executeContext, variableContext);
        return executorResult;
    }

    private List<Long> queryCallInvocation(String traceId, Long processId) {
        if (StringUtils.isEmpty(traceId)) {
            throw new IllegalArgumentException("traceId is empty");
        }
        String key = FlowUtils.toRedisTraceKey(traceId);
        RList<Long> list = redissonClient.getList(key);
        list.expire(FlowUtils.REDIS_TRACE_TIMEOUT);
        list.add(processId);
        List<Long> result = new ArrayList<>(list);
        return result;
    }

    private static ExecutorResult getExecutorResult(LiteflowResponse response, ExecuteContext executeContext, VariableContext variableContext) {
        ExecutorResult executorResult = new ExecutorResult();
        executorResult.setSuccess(response.isSuccess());
        executorResult.setCode(response.getCode());
        executorResult.setMessage(response.getMessage());
        executorResult.setCause(response.getCause());
        executorResult.setExecutionEnd(executeContext.isExecuteEnd());
        executorResult.setExecutionUuid(executeContext.getExecutionUuid());
        executorResult.setExecutionEndNodeType(executeContext.getExecutionEndNodeType());
        executorResult.setExecutionEndNodeTag(executeContext.getExecutionEndNodeTag());
        executorResult.setOutputParams(variableContext.getOutputParams());
        return executorResult;
    }

    public ExecutorResult execute(Long processId, String executionUuid, Map<String, Object> uuidFiles) {
        String chainId = FlowUtils.toFlowChainId(processId);
        VariableContext variableContext = contextProvider.restoreVariableContext(executionUuid);
        if (variableContext == null) {
            throw new IllegalArgumentException("执行上下文不存在: " + executionUuid);
        }
        variableContext.setUuidFiles(uuidFiles);
        variableContext.setOutputParams(new HashMap<>());
        ExecuteContext executeContext = contextProvider.restoreExecuteContext(executionUuid);
        if (executeContext == null) {
            throw new IllegalArgumentException("执行上下文不存在: " + executionUuid);
        }
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        variableContext = response.getContextBean(VariableContext.class);
        executeContext = response.getContextBean(ExecuteContext.class);
        ExecutorResult executorResult = getExecutorResult(response, executeContext, variableContext);
        return executorResult;
    }
}

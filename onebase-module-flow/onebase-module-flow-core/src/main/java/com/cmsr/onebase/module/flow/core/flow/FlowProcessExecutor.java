package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.ContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.google.common.collect.Maps;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

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

    public ExecutorResult execute(Long processId, Map<String, Object> inputParams) {
        String chainId = FlowUtils.toFlowChainId(processId);
        VariableContext variableContext = new VariableContext();
        variableContext.setInputParams(inputParams);

        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setProcessId(processId);
        executeContext.setNodeDataMap(graphFlowCache.findNodeData(processId));
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        variableContext = response.getContextBean(VariableContext.class);
        executeContext = response.getContextBean(ExecuteContext.class);
        ExecutorResult executorResult = getExecutorResult(response, executeContext, variableContext);
        return executorResult;
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

    public ExecutorResult execute(Long processId, String executionUuid, Map<String, Object> inputMap) throws Exception {
        String chainId = FlowUtils.toFlowChainId(processId);
        VariableContext variableContext = contextProvider.restoreVariableContext(executionUuid);
        variableContext.setOutputParams(Maps.newHashMap());
        ExecuteContext executeContext = contextProvider.restoreExecuteContext(executionUuid);
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        variableContext = response.getContextBean(VariableContext.class);
        executeContext = response.getContextBean(ExecuteContext.class);
        ExecutorResult executorResult = getExecutorResult(response, executeContext, variableContext);
        return executorResult;
    }
}

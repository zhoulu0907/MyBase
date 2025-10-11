package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.graph.GraphFlowCache;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
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

    public ExecutorResult execute(Long processId, Map<String, Object> inputParams) {
        String chainId = FlowUtils.toFlowChainId(processId);
        VariableContext variableContext = new VariableContext();
        variableContext.setInputParams(inputParams);

        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setProcessId(processId);
        executeContext.setNodeDataMap(graphFlowCache.findNodeData(processId));
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        ExecutorResult executorResult = new ExecutorResult();
        executorResult.setSuccess(response.isSuccess());
        executorResult.setCode(response.getCode());
        executorResult.setMessage(response.getMessage());
        executorResult.setCause(response.getCause());

        executeContext = response.getContextBean(ExecuteContext.class);
        executorResult.setExecutionEnd(executeContext.isExecuteEnd());
        VariableContext resultContext = response.getContextBean(VariableContext.class);
        executorResult.setOutputParams(resultContext.getOutputParams());
        return executorResult;
    }

}

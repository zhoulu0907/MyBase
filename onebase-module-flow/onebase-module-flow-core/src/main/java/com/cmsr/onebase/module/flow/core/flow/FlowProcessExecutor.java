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

    public Map<String, Object> execute(Long processId, Map<String, Object> inputParams) {
        String chainId = FlowUtils.toFlowChainId(processId);
        VariableContext variableContext = new VariableContext();
        variableContext.setInputParams(inputParams);

        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setProcessId(processId);
        executeContext.setNodeDataMap(graphFlowCache.getNodeData(processId));
        LiteflowResponse response = flowExecutor.execute2Resp(chainId, processId, variableContext, executeContext);
        VariableContext resultContext = response.getContextBean(VariableContext.class);
        return resultContext.getOutputParams();
    }

}

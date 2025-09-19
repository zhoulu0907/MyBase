package com.cmsr.onebase.module.flow.core.flow;

import com.cmsr.onebase.module.flow.core.config.FlowRuntimeCondition;
import com.cmsr.onebase.module.flow.core.utils.FlowUtils;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
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

    @Autowired(required = false)
    private FlowExecutor flowExecutor;

    public Map<String, Object> execute(Long processId, Map<String, Object> inputParams) {
        String chainId = FlowUtils.toFlowChainId(processId);
        DefaultContext defaultContext = new DefaultContext();
        defaultContext.setData(FlowUtils.INPUT, inputParams);

        ExecuteContext executeContext = new ExecuteContext();
        executeContext.setProcessId(processId);

        LiteflowResponse response = flowExecutor.execute2Resp(chainId, "", defaultContext, executeContext);
        DefaultContext resultContext = response.getContextBean(DefaultContext.class);
        return resultContext.getDataMap();
    }

}

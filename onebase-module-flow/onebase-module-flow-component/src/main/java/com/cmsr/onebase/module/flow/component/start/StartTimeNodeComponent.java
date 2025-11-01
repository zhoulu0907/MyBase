package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("startTime")
public class StartTimeNodeComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("时间触发开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
    }

}

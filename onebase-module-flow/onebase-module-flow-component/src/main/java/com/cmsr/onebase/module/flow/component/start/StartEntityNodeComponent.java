package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.module.flow.context.VariableContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/16 10:58
 */
@Slf4j
@LiteflowComponent("startEntity")
public class StartEntityNodeComponent extends NodeComponent {
    @Override
    public void process() throws Exception {
        log.info("StartEntityNodeComponent process");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
    }
}

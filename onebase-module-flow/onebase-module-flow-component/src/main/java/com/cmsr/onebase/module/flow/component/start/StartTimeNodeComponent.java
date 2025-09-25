package com.cmsr.onebase.module.flow.component.start;

import com.cmsr.onebase.module.flow.component.NormalNodeComponent;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("startTime")
public class StartTimeNodeComponent extends NormalNodeComponent {


    @Override
    public void process() throws Exception {
        log.info("StartTimeNodeComponent process");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        variableContext.putInputVariables(this.getTag());
    }

}

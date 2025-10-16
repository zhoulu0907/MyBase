package com.cmsr.onebase.module.flow.component.sys;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("end")
public class EndNodeComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        log.info("EndNodeComponent process");
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.setExecuteEnd(true);
    }

}

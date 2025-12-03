package com.cmsr.onebase.module.flow.component.sys;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.EndNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author：huangjie
 * @Date：2025/9/5 9:24
 */
@Slf4j
@LiteflowComponent("end")
public class EndNodeComponent extends NodeComponent {

    @Override
    public void process() throws Exception {
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("结束节点开始执行");
        executeContext.setExecuteEnd(true);
        super.setIsEnd(true);
        EndNodeData nodeData = (EndNodeData) executeContext.getNodeData(this.getTag());
        // 设置异常终止，默认为正常终止，所以尽量设置为 true
        if (StringUtils.equalsIgnoreCase(nodeData.getStatusCode(), "false")) {
            executeContext.setAbnormalTermination(Boolean.TRUE);
            executeContext.setTerminationMessage(nodeData.getPrompt());
        }
    }

}

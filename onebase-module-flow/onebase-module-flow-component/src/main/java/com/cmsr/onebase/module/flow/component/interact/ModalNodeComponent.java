package com.cmsr.onebase.module.flow.component.interact;

import com.cmsr.onebase.module.flow.component.NodeActionEnum;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.context.provider.FlowContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Map;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("modal")
public class ModalNodeComponent extends SkippableNodeComponent {

    @Autowired
    private FlowContextProvider flowContextProvider;

    @Override
    public void process() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        executeContext.addLog("弹窗节点开始执行");
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        ModalNodeData nodeData = (ModalNodeData) executeContext.getNodeData(this.getTag());
        NodeActionEnum nodeActionEnum = super.nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            Map<String, Object> outputParams = nodeData.toMap();
            variableContext.setOutputParams(outputParams);
            executeContext.setExecutionEndNodeType("modal");
            executeContext.setExecutionEndNodeTag(this.getTag());
            flowContextProvider.storeExecuteContext(executeContext.getExecutionUuid(), executeContext);
            flowContextProvider.storeVariableContext(executeContext.getExecutionUuid(), variableContext);
            executeContext.addLog("弹窗节点返回暂停执行, 返回窗口信息");
            this.setIsEnd(true);
        }
        if (nodeActionEnum == NodeActionEnum.DO_RESET) {
            executeContext.addLog("弹窗节点返回重置执行");
            variableContext.putNodeVariables(this.getTag(), variableContext.getInputFields());
            variableContext.setInputFields(Collections.emptyMap());
            executeContext.restExecutionUuid();
            executeContext.restExecutionEndNodeTag();
        }
    }

}

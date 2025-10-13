package com.cmsr.onebase.module.flow.component.interact;

import com.cmsr.onebase.module.flow.component.NodeActionEnum;
import com.cmsr.onebase.module.flow.component.SkippableNodeComponent;
import com.cmsr.onebase.module.flow.context.ContextProvider;
import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.cmsr.onebase.module.flow.context.VariableContext;
import com.cmsr.onebase.module.flow.context.graph.nodes.ModalNodeData;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Author：huangjie
 * @Date：2025/9/26 14:01
 */
@Setter
@LiteflowComponent("modal")
public class ModalNodeComponent extends SkippableNodeComponent {

    @Autowired
    private ContextProvider contextProvider;

    @Override
    public void process() throws Exception {
        // 获取上下文和节点数据
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        VariableContext variableContext = this.getContextBean(VariableContext.class);
        ModalNodeData nodeData = (ModalNodeData) executeContext.getNodeData(this.getTag());
        NodeActionEnum nodeActionEnum = super.nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            Map<String, Object> outputParams = createOutputParams(nodeData);
            variableContext.setOutputParams(outputParams);
            String executionUuid = UUID.randomUUID().toString();
            executeContext.setExecutionUuid(executionUuid);
            executeContext.setExecutionEndTag(this.getTag());
            contextProvider.storeExecuteContext(executionUuid, executeContext);
            contextProvider.storeVariableContext(executionUuid, variableContext);
            this.setIsEnd(true);
        }
        if (nodeActionEnum == NodeActionEnum.DO_RESET) {
            executeContext.restExecutionUuid();
            executeContext.restExecutionEndTag();
        }
    }

    private Map<String, Object> createOutputParams(ModalNodeData nodeData) {
        Map<String, Object> outputParams = new HashMap<>();
        outputParams.put("closeWarn", nodeData.getCloseWarn());
        outputParams.put("modalType", nodeData.getModalType());
        outputParams.put("cancelWarn", nodeData.getCancelWarn());
        outputParams.put("modalTitle", nodeData.getModalTitle());
        outputParams.put("cancelText", nodeData.getCancelText());
        outputParams.put("title", nodeData.getTitle());
        outputParams.put("prompt", nodeData.getPrompt());
        outputParams.put("afterCancel", nodeData.getAfterCancel());
        return outputParams;
    }


}

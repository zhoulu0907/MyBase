package com.cmsr.onebase.module.flow.component;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * @Author：huangjie
 * @Date：2025/9/5 17:23
 */
public abstract class SkippableNodeComponent extends NodeComponent {

    public NodeActionEnum nodeAction() {
        ExecuteContext contextBean = this.getContextBean(ExecuteContext.class);
        String tag = this.getTag();
        if (contextBean.isExecutionEndTagEmpty()) {
            return NodeActionEnum.DO_PROCESS;
        } else if (!contextBean.isExecutionEndTagEmpty() && contextBean.executionEndTagEquals(tag)) {
            return NodeActionEnum.REST_AND_SKIP;
        } else if (!contextBean.isExecutionEndTagEmpty() && !contextBean.executionEndTagEquals(tag)) {
            return NodeActionEnum.DO_SKIP;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }

    @Override
    public boolean isAccess() {
        NodeActionEnum nodeActionEnum = nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            return true;
        } else if (nodeActionEnum == NodeActionEnum.DO_SKIP) {
            return false;
        } else if (nodeActionEnum == NodeActionEnum.REST_AND_SKIP) {
            return true;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }


}

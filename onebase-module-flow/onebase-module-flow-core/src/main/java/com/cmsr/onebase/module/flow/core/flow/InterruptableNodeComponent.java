package com.cmsr.onebase.module.flow.core.flow;

import com.yomahub.liteflow.core.NodeComponent;

/**
 * @Author：huangjie
 * @Date：2025/9/5 17:23
 */
public abstract class InterruptableNodeComponent extends NodeComponent {

    public NodeActionEnum nodeAction() {
        ExecuteContext contextBean = this.getContextBean(ExecuteContext.class);
        if (contextBean.getPreviousNodeTag().isEmpty()) {
            return NodeActionEnum.DO_PROCESS;
        } else if (contextBean.getPreviousNodeTag().isPresent() &&
                contextBean.equalsPreviousNodeTag(this.getTag())) {
            return NodeActionEnum.SKIP_AND_REST;
        } else {
            return NodeActionEnum.DO_SKIP;
        }
    }

    @Override
    public boolean isAccess() {
        NodeActionEnum nodeActionEnum = nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            return true;
        } else if (nodeActionEnum == NodeActionEnum.DO_SKIP) {
            return false;
        } else if (nodeActionEnum == NodeActionEnum.SKIP_AND_REST) {
            ExecuteContext contextBean = this.getContextBean(ExecuteContext.class);
            contextBean.restPreviousNodeTag();
            return false;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }


}

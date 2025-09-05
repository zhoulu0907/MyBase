package com.cmsr.onebase.module.flow.flow;

import com.yomahub.liteflow.core.NodeComponent;

/**
 * @Author：huangjie
 * @Date：2025/9/5 17:23
 */
public abstract class InterruptableNodeComponent extends NodeComponent {

    public NodeActionEnum nodeAction() {
        ExecuteContext contextBean = this.getContextBean(ExecuteContext.class);
        if (contextBean.getInterruptNodeTag().isEmpty()) {
            return NodeActionEnum.DO_PROCESS;
        } else if (contextBean.getInterruptNodeTag().isPresent() &&
                contextBean.equalsInterruptNodeTag(this.getTag())) {
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
            contextBean.restInterruptNodeTag();
            return false;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }


}

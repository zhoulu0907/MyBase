package com.cmsr.onebase.module.flow.component;

import com.cmsr.onebase.module.flow.context.ExecuteContext;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * @Author：huangjie
 * @Date：2025/9/5 17:23
 */
public abstract class SkippableNodeComponent extends NodeComponent {

    /**
     * TODO 考虑几种情况，提前抽象方法，比如 doProcess()、doSkip()、doReset()等
     *
     * @return
     */
    @Override
    public boolean isAccess() {
        NodeActionEnum nodeActionEnum = nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            return true;
        } else if (nodeActionEnum == NodeActionEnum.DO_SKIP) {
            return false;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }

    /**
     * 根据执行上下文和节点标签确定节点行为
     * <p>
     * 重置状态：就是把ExecutionEndTag设置为空
     *
     * @return NodeActionEnum 节点行为枚举值
     */
    public NodeActionEnum nodeAction() {
        // 获取执行上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        // 获取当前节点标签
        String tag = this.getTag();
        if (executeContext.isDebugMode()) {
            // 调试模式下的处理逻辑

        } else {
            // 非调试模式下的处理逻辑
            Object processResult = executeContext.getNodeProcessResult(tag);
            if (processResult == null) {
                return NodeActionEnum.DO_PROCESS;
            } else if (processResult instanceof NodeResultEnum nodeActionEnum) {
                if (nodeActionEnum == NodeResultEnum.COMPLETED) {
                    return NodeActionEnum.DO_SKIP;
                }
                if (nodeActionEnum == NodeResultEnum.NEED_REEXECUTE) {
                    return NodeActionEnum.DO_PROCESS;
                }
            } else {
                return NodeActionEnum.DO_PROCESS;
            }
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }

    @Override
    public void onSuccess() throws Exception {
        super.onSuccess();
        // 获取执行上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        // 获取当前节点标签
        String tag = this.getTag();
        Object processResult = executeContext.getNodeProcessResult(tag);
        if (processResult == null) {
            // 如果没有指定结果，则默认为完成
            processResult = NodeResultEnum.COMPLETED;
        }

        if (executeContext.isDebugMode()) {
            // 调试模式下的处理逻辑

        } else {
            executeContext.putNodeProcessResult(tag, processResult);
        }

    }
}

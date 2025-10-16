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
     * @return
     */
    @Override
    public boolean isAccess() {
        NodeActionEnum nodeActionEnum = nodeAction();
        if (nodeActionEnum == NodeActionEnum.DO_PROCESS) {
            return true;
        } else if (nodeActionEnum == NodeActionEnum.DO_SKIP) {
            return false;
        } else if (nodeActionEnum == NodeActionEnum.DO_RESET) {
            return true;
        } else if (nodeActionEnum == NodeActionEnum.DO_PROCESS_AND_DO_END) {
            return true;
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }

    /**
     * 根据执行上下文和节点标签确定节点行为
     *
     * 重置状态：就是把ExecutionEndTag设置为空
     * @return NodeActionEnum 节点行为枚举值
     */
    public NodeActionEnum nodeAction() {
        // 获取执行上下文
        ExecuteContext executeContext = this.getContextBean(ExecuteContext.class);
        // 获取当前节点标签
        String tag = this.getTag();
        
        if (executeContext.isDebugMode()) {
            // 调试模式下的处理逻辑
            if (executeContext.isExecutionEndNodeTagEmpty()) {
                // 如果执行结束标签为空，则执行当前节点并结束后续流程
                return NodeActionEnum.DO_PROCESS_AND_DO_END;
            } else if (!executeContext.isExecutionEndNodeTagEmpty() && !executeContext.isExecutionEndNodeTagEquals(tag)) {
                // 如果执行结束标签不为空且不等于当前节点标签，则跳过当前节点
                return NodeActionEnum.DO_SKIP;
            } else if (!executeContext.isExecutionEndNodeTagEmpty() && executeContext.isExecutionEndNodeTagEquals(tag)) {
                // 如果执行结束标签不为空且等于当前节点标签，则重置状态，便于后续节点执行
                return NodeActionEnum.DO_RESET;
            }
        } else {
            // 非调试模式下的处理逻辑
            if (executeContext.isExecutionEndNodeTagEmpty()) {
                // 如果执行结束标签为空，则正常执行当前节点
                return NodeActionEnum.DO_PROCESS;
            } else if (!executeContext.isExecutionEndNodeTagEmpty() && !executeContext.isExecutionEndNodeTagEquals(tag)) {
                // 如果执行结束标签不为空且不等于当前节点标签，则跳过当前节点
                return NodeActionEnum.DO_SKIP;
            } else if (!executeContext.isExecutionEndNodeTagEmpty() && executeContext.isExecutionEndNodeTagEquals(tag)) {
                // 如果执行结束标签不为空且等于当前节点标签，则重置重置状态，便于后续节点执行
                return NodeActionEnum.DO_RESET;
            }
        }
        throw new IllegalStateException("nodeAction()返回值异常");
    }


}

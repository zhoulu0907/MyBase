import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { ConfigProvider } from '@douyinfe/semi-ui';
import { FlowNodeEntity, useNodeRender } from '@flowgram.ai/fixed-layout-editor';
import { NodeType } from '@onebase/common';
import { useSignals } from '@preact/signals-react/runtime';
import { useCallback } from 'react';
import { NodeRenderContext } from '../../context';
import styles from './index.module.less';
import { ErrorIcon } from './styles';

export const BaseNode = ({ node }: { node: FlowNodeEntity }) => {
  useSignals();

  /**
   * Provides methods related to node rendering
   * 提供节点渲染相关的方法
   */
  const nodeRender = useNodeRender();
  /**
   * It can only be used when nodeEngine is enabled
   * 只有在节点引擎开启时候才能使用表单
   */
  const form = nodeRender.form;

  /**
   * Used to make the Tooltip scale with the node, which can be implemented by itself depending on the UI library
   * 用于让 Tooltip 跟随节点缩放, 这个可以根据不同的 ui 库自己实现
   */
  const getPopupContainer = useCallback(() => node.renderData.node || document.body, []);

  /**
   * Sidebar control
   */
  const { setNodeId } = triggerEditorSignal;

  return (
    <ConfigProvider getPopupContainer={getPopupContainer}>
      {triggerEditorSignal.isInvalidNode(node.id) && <ErrorIcon />}

      <div
        /*
         * onMouseEnter is added to a fixed layout node primarily to listen for hover highlighting of branch lines
         * onMouseEnter 加到固定布局节点主要是为了监听 分支线条的 hover 高亮
         **/
        onMouseEnter={nodeRender.onMouseEnter}
        onMouseLeave={nodeRender.onMouseLeave}
        className={
          // if-block 样式 没有边框 阴影
          (nodeRender.activated || nodeRender.node.id === triggerEditorSignal.nodeId.value) &&
          !triggerEditorSignal.isInvalidNode(node.id)
            ? `${styles.baseNodeStyle} ${styles.activated} ${nodeRender.type === NodeType.IF_BLOCK ? styles.noShadow : ''}`
            : `${styles.baseNodeStyle} ${nodeRender.type === NodeType.IF_BLOCK ? styles.noShadow : ''}`
        }
        onClick={() => {
          if (nodeRender.dragging) {
            return;
          }
          console.log('onClick', nodeRender.node.id);
          setNodeId(nodeRender.node.id);
        }}
        style={{
          /**
           * Lets you precisely control the style of branch nodes
           * 用于精确控制分支节点的样式
           * isBlockIcon: 整个 condition 分支的 头部节点
           * isBlockOrderIcon: 分支的第一个节点
           */
          ...nodeRender.node.getNodeRegistry().meta.style,
          opacity: nodeRender.dragging ? 0.3 : 1,
          outline: triggerEditorSignal.isInvalidNode(node.id) ? '1px solid rgb(var(--red-6))' : 'none'
        }}
      >
        <NodeRenderContext.Provider value={nodeRender}>{form?.render()}</NodeRenderContext.Provider>
      </div>
    </ConfigProvider>
  );
};

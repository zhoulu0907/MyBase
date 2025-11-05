import { triggerEditorSignal } from '@/store/singals/trigger_editor';
import { ConfigProvider } from '@douyinfe/semi-ui';
import { FlowNodeEntity, useNodeRender } from '@flowgram.ai/free-layout-editor';
import { useSignals } from '@preact/signals-react/runtime';
import { useCallback } from 'react';
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
        className={`${styles.baseNodeStyle}`}
        onClick={() => {
          console.log('onClick', node.id);
          setNodeId(node.id);
        }}
        style={{
          /**
           * Lets you precisely control the style of branch nodes
           * 用于精确控制分支节点的样式
           */
          ...node.getNodeRegistry().meta.style,
          outline: triggerEditorSignal.isInvalidNode(node.id) ? '1px solid rgb(var(--red-6))' : 'none'
        }}
      >
        {form?.render()}
      </div>
    </ConfigProvider>
  );
};
